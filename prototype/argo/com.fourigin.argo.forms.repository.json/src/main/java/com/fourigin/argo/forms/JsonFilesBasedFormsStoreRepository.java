package com.fourigin.argo.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.models.Attachment;
import com.fourigin.argo.forms.models.FormsDataProcessingState;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import com.fourigin.argo.forms.models.FormsStoreEntryHeader;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingHistory;
import com.fourigin.argo.forms.models.ProcessingState;
import com.fourigin.utilities.core.JsonFileBasedRepository;
import de.huxhorn.sulky.blobs.AmbiguousIdException;
import de.huxhorn.sulky.blobs.impl.BlobRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class JsonFilesBasedFormsStoreRepository extends JsonFileBasedRepository implements FormsStoreRepository {
    private BlobRepositoryImpl blobRepository;

    private static final String DIR_BLOBS = ".blobs";

    private static final String DIR_ATTACHMENTS = "attachments";

    private final Logger logger = LoggerFactory.getLogger(JsonFilesBasedFormsStoreRepository.class);

    public JsonFilesBasedFormsStoreRepository(File baseDirectory, ObjectMapper objectMapper) {
        setBaseDirectory(baseDirectory);
        setObjectMapper(objectMapper);

        File blobBase = new File(baseDirectory, DIR_BLOBS);
        if (!blobBase.exists() && !blobBase.mkdirs()) {
            throw new IllegalArgumentException("Unable to create blobs base directory '" + blobBase.getAbsolutePath() + "'!");
        }

        blobRepository = new BlobRepositoryImpl();
        blobRepository.setBaseDirectory(blobBase);
    }

    @Override
    public String createEntry(FormsStoreEntry entry) {
        Objects.requireNonNull(entry, "entry must not be null!");

        String entryId;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            getObjectMapper().writeValue(baos, entry.getData());

            byte[] bytes = baos.toByteArray();

            entryId = blobRepository.put(bytes);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to serialize form data!", ex);
        }


        return entryId;
    }

    @Override
    public void deleteEntry(String entryId) {
        try {
            if (blobRepository.contains(entryId)) {
                blobRepository.delete(entryId);
            }
        } catch (AmbiguousIdException ex) {
            throw new IllegalArgumentException("Something wrong...", ex);
        }
    }

    @Override
    public FormsStoreEntry retrieveEntry(String entryId) {
        Objects.requireNonNull(entryId, "entryId must not be null!");

        Map<String, Object> data;
        try (InputStream is = blobRepository.get(entryId)) {
            data = getObjectMapper().readValue(is, FormsData.class);
        } catch (Throwable th) {
            throw new IllegalArgumentException("Unable to retrieve entry for id '" + entryId + "'!", th);
        }

        FormsStoreEntry entry = new FormsStoreEntry();
        entry.setId(entryId);
        entry.setData(data);

        return entry;
    }

    @Override
    public void createEntryInfo(String entryId, FormsStoreEntryHeader header) {
        Objects.requireNonNull(entryId, "entryId must not be null!");
        Objects.requireNonNull(header, "header must not be null!");

        FormsStoreEntryInfo info = new FormsStoreEntryInfo();
        info.setId(entryId);
        info.setRevision(1L);
        info.setHeader(header);

        write(info, entryId);
    }

    @Override
    public FormsStoreEntryInfo retrieveEntryInfo(String entryId) {
        Objects.requireNonNull(entryId, "entryId must not be null!");

        return read(FormsStoreEntryInfo.class, entryId);
    }

    @Override
    public void addProcessingState(String entryId, String processorName, ProcessingState state) {
        addProcessingState(entryId, processorName, state, null);
    }

    @Override
    public void addProcessingState(String entryId, String processorName, ProcessingState state, Map<String, String> context) {
        FormsStoreEntryInfo info = read(FormsStoreEntryInfo.class, entryId);

        Map<String, FormsDataProcessingState> states = info.getProcessingStates();
        FormsDataProcessingState formDataProcessingState = states.get(processorName);
        if (formDataProcessingState == null) {
            formDataProcessingState = new FormsDataProcessingState();
            states.put(processorName, formDataProcessingState);
        }
        formDataProcessingState.setProcessingState(state);

        List<ProcessingHistory> history = formDataProcessingState.getProcessingHistory();
        if (history == null) {
            history = new ArrayList<>();
            formDataProcessingState.setProcessingHistory(history);
        }

        ProcessingHistory historyEntry = new ProcessingHistory();
        historyEntry.setTimestamp(System.currentTimeMillis());
        if (context != null) {
            historyEntry.setContext(context);
        }
        history.add(historyEntry);

        write(info, entryId);
    }

    @Override
    public Set<String> getAttachmentNames(String entryId) {
        File directory = new File(getBaseDirectory() + "/" + resolveBasePath(entryId) + "/" + DIR_ATTACHMENTS);
        if(!directory.exists()){
            return Collections.emptySet();
        }

        Set<String> names = new HashSet<>();
        String[] fileNames = directory.list((dir, name) -> dir.equals(directory) && name.endsWith(".json"));
        if(fileNames != null && fileNames.length > 0){
            for (String fileName : fileNames) {
                names.add(fileName.substring(0, fileName.indexOf(".json")));
            }
        }

        return names;
    }

    @Override
    public void addAttachment(String entryId, String name, Attachment attachment) {
        write(attachment, entryId, name);
    }

    @Override
    public Attachment getAttachment(String entryId, String name) {
        return read(Attachment.class, entryId, name);
    }

    @Override
    public void deleteAttachment(String entryId, String name) {
        File file = getFile(Attachment.class, entryId, name);
        if (!file.exists()) {
            if (logger.isErrorEnabled()) logger.error("No attachment file found for id {} and name {}", entryId, name);
            return;
        }

        if (!file.delete()) {
            throw new IllegalStateException("Unable to delete attachment file '" + file.getAbsolutePath() + "'!");
        }
    }

    @Override
    protected <T> File getFile(Class<T> target, String id, String... path) {
        if (FormsStoreEntryInfo.class.isAssignableFrom(target)) {
            // info
            File directory = new File(getBaseDirectory(), resolveBasePath(id));
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IllegalStateException("Unable to create missing info directory '" + directory.getAbsolutePath() + "'!");
            }

            String targetFile = "info.json";
            return new File(directory, targetFile);
        }

        if (Attachment.class.isAssignableFrom(target)) {
            // attachments
            File directory = new File(getBaseDirectory() + "/" + resolveBasePath(id) + "/" + DIR_ATTACHMENTS);

            if (!directory.exists() && !directory.mkdirs()) {
                throw new IllegalStateException("Unable to create missing attachments directory '" + directory.getAbsolutePath() + "'!");
            }

            String attachmentFile = path[0] + ".json";
            return new File(directory, attachmentFile);
        }

        throw new UnsupportedOperationException("Target class '" + target.getName() + "' not supported!");
    }
}

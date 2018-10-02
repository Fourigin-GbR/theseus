package com.fourigin.argo.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import com.fourigin.argo.forms.models.FormsStoreEntryHeader;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingState;
import com.fourigin.utilities.core.JsonFileBasedRepository;
import de.huxhorn.sulky.blobs.AmbiguousIdException;
import de.huxhorn.sulky.blobs.impl.BlobRepositoryImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class JsonFilesBasedFormsStoreRepository extends JsonFileBasedRepository implements FormsStoreRepository {
    private BlobRepositoryImpl blobRepository;

    private static final String DIR_BLOBS = ".blobs";

    private static final String DIR_ATTACHMENTS = "attachments";

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

    }

    @Override
    public void addProcessingState(String entryId, String processorName, ProcessingState state, Map<String, String> context) {

    }

    @Override
    public Set<String> getAttachmentNames(String entryId) {
        return null;
    }

    @Override
    public void addAttachment(String entryId, String name, Object attachment) {

    }

    @Override
    public <T> T getAttachment(String entryId, String name, Class<T> target) {
        return null;
    }

    @Override
    public void deleteAttachment(String entryId, String name) {

    }

    @Override
    protected <T> File getDataFileBase(Class<T> target, String id, String... path) {
        return new File(getBaseDirectory(), resolveBasePath(id));
    }

    @Override
    protected <T> String getDataFileName(Class<T> target, String... path) {
        if (FormsStoreEntryInfo.class.isAssignableFrom(target)) {
            return "info";
        }

        return null;
    }

//    public static String resolveFormEntryBasePath(String entryId){
//        String firstBlobPart = entryId.substring(0,2);
//        String remainingBlobPart = entryId.substring(2);
//        return firstBlobPart + "/" + remainingBlobPart;
//    }
//
//    /* private -> testing */
//    File getInfoFile(String entryId) {
//        File entryDirectory = new File(baseDirectory, resolveFormEntryBasePath(entryId));
//
//        if (!entryDirectory.exists() && !entryDirectory.mkdirs()) {
//            throw new IllegalStateException("Unable to create missing entry directory '" + entryDirectory.getAbsolutePath() + "'!");
//        }
//
//        String propsFile = "info.json";
//        return new File(entryDirectory, propsFile);
//    }
//
//    /* private -> testing */
//    FormsStoreEntryInfo readInfo(String entryId) {
//        File infoFile = getInfoFile(entryId);
//        if (!infoFile.exists()) {
//            return null;
//        }
//
//        ReadWriteLock lock = getLock(entryId);
//        lock.readLock().lock();
//
//        try (InputStream is = new BufferedInputStream(new FileInputStream(infoFile))) {
//            return objectMapper.readValue(is, FormsStoreEntryInfo.class);
//        } catch (IOException ex) {
//            // TODO: create proper exception handling
//            throw new IllegalArgumentException("Error reading entry info from file (" + infoFile.getAbsolutePath() + ")!", ex);
//        } finally {
//            lock.readLock().unlock();
//        }
//    }
//
//    /* private -> testing */
//    void writeInfo(String entryId, FormsStoreEntryInfo info) {
//        File infoFile = getInfoFile(entryId);
//
//        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(infoFile))) {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, info);
//        } catch (IOException ex) {
//            // TODO: create proper exception handling
//            throw new IllegalArgumentException("Error writing entry info to file (" + infoFile.getAbsolutePath() + ")!", ex);
//        }
//    }
}

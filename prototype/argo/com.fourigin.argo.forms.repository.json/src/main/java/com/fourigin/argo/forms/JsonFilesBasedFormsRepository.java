package com.fourigin.argo.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.definition.FieldDefinition;
import com.fourigin.argo.forms.definition.FormDefinition;
import com.fourigin.argo.forms.definition.ProcessingStages;
import com.fourigin.argo.forms.model.FormsData;
import com.fourigin.argo.forms.model.StringList;
import com.fourigin.argo.forms.models.Attachment;
import com.fourigin.argo.forms.models.FormsDataProcessingRecord;
import com.fourigin.argo.forms.models.FormsEntryHeader;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingState;
import com.fourigin.utilities.core.JsonFileBasedRepository;
import com.fourigin.utilities.core.MimeTypes;
import de.huxhorn.sulky.blobs.AmbiguousIdException;
import de.huxhorn.sulky.blobs.impl.BlobRepositoryImpl;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

public class JsonFilesBasedFormsRepository extends JsonFileBasedRepository
        implements FormsStoreRepository, FormDefinitionResolver, CustomerRepository {

    private BlobRepositoryImpl blobRepository;

    private File definitionsBaseDir;

    private File customersBaseDir;

    private Set<String> entryIds;

    private static final String DIR_BLOBS = ".blobs";

    private static final String DIR_ATTACHMENTS = "attachments";

    private static final String DIR_DEFINITIONS = ".definitions";

    private static final String DIR_CUSTOMERS = ".customers";

    private final Logger logger = LoggerFactory.getLogger(JsonFilesBasedFormsRepository.class);

    public JsonFilesBasedFormsRepository(File baseDirectory, ObjectMapper objectMapper) {
        setBaseDirectory(baseDirectory);
        setObjectMapper(objectMapper);

        File blobBase = new File(baseDirectory, DIR_BLOBS);
        if (!blobBase.exists() && !blobBase.mkdirs()) {
            throw new IllegalArgumentException("Unable to create blobs base directory '" + blobBase.getAbsolutePath() + "'!");
        }

        blobRepository = new BlobRepositoryImpl();
        blobRepository.setBaseDirectory(blobBase);

        definitionsBaseDir = new File(baseDirectory, DIR_DEFINITIONS);
        if (!definitionsBaseDir.exists() && !definitionsBaseDir.mkdirs()) {
            throw new IllegalArgumentException("Unable to create form-definitions base directory '" + definitionsBaseDir.getAbsolutePath() + "'!");
        }

        customersBaseDir = new File(baseDirectory, DIR_CUSTOMERS);
        if (!customersBaseDir.exists() && !customersBaseDir.mkdirs()) {
            throw new IllegalArgumentException("Unable to create customers base directory '" + customersBaseDir.getAbsolutePath() + "'!");
        }
    }

    private void updateIdsIndex() {
        if (logger.isDebugEnabled()) logger.debug("Updating ids index");

        ReadWriteLock lock = getLock("ids-index");
        lock.writeLock().lock();

        File file = new File(getBaseDirectory(), "ids-index.json");
        if (logger.isDebugEnabled()) logger.debug("Writing index file '{}'", file.getAbsolutePath());

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            StringList data = new StringList();
            data.addAll(entryIds);
            getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(os, data);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error writing ids index to file (" + file.getAbsolutePath() + ")!", ex);
        } finally {
            lock.writeLock().unlock();
            if (logger.isDebugEnabled()) logger.debug("Writing done.");
        }
    }

    @Override
    public Collection<String> listEntryIds() {
        if (logger.isDebugEnabled()) logger.debug("Reading entry ids");

        ReadWriteLock lock = getLock("ids-index");
        lock.readLock().lock();

        File file = new File(getBaseDirectory(), "ids-index.json");
        if (logger.isDebugEnabled()) logger.debug("Reading index file '{}'", file.getAbsolutePath());

        boolean changed = false;
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            StringList idList = getObjectMapper().readValue(is, StringList.class);
            entryIds = new HashSet<>(idList);
        } catch (IOException ex) {
            if (logger.isDebugEnabled()) logger.debug("Re-initializing entryIds view, reading failed", ex);
            entryIds = new HashSet<>();

            Set<String> blobs = blobRepository.idSet();
            for (String blob : blobs) {
                FormsStoreEntryInfo info = retrieveEntryInfo(blob);
                if (info == null) {
                    if (logger.isDebugEnabled()) logger.debug("Skipping a non-base blob id {}", blob);
                    continue;
                }

                if (logger.isDebugEnabled())
                    logger.debug("Adding a valid entry-id {} with current revision {}", blob, info.getRevision());

                entryIds.add(blob);
            }

            changed = true;
        } finally {
            lock.readLock().unlock();
            if (logger.isDebugEnabled()) logger.debug("Reading done.");
        }

        if (changed) {
            updateIdsIndex();
        }

        if (logger.isDebugEnabled()) logger.debug("Actual entry ids: {}", entryIds);
        return entryIds;
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

        // update entryIds view
        entryIds.add(entryId);
        updateIdsIndex();

        entry.setId(entryId);
        return entryId;
    }

    @Override
    public void deleteEntry(FormsStoreEntryInfo info) {
        List<String> dataVersions = info.getDataVersions();
        if (dataVersions == null || dataVersions.isEmpty()) {
            return;
        }

        for (String dataVersion : dataVersions) {
            try {
                if (blobRepository.contains(dataVersion)) {
                    blobRepository.delete(dataVersion);
                }
            } catch (AmbiguousIdException ex) {
                throw new IllegalArgumentException("Something wrong...", ex);
            }
        }
    }

    @Override
    public void updateEntry(FormsStoreEntryInfo info, FormsStoreEntry entry) {
        Objects.requireNonNull(entry, "entry must not be null!");

        String dataVersion;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            getObjectMapper().writeValue(baos, entry.getData());

            byte[] bytes = baos.toByteArray();

            dataVersion = blobRepository.put(bytes);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to serialize form data!", ex);
        }

        info.setRevision(info.getRevision() + 1);
        info.getDataVersions().add(dataVersion);
    }

    @Override
    public FormsStoreEntry retrieveEntry(FormsStoreEntryInfo info) {
        Objects.requireNonNull(info, "info must not be null!");

        String dataVersion = getCurrentDataVersion(info);

        Map<String, String> data;
        try (InputStream is = blobRepository.get(dataVersion)) {
            data = getObjectMapper().readValue(is, FormsData.class);
        } catch (Throwable th) {
            throw new IllegalArgumentException("Unable to retrieve entry for id '" + dataVersion + "'!", th);
        }

        FormsStoreEntry entry = new FormsStoreEntry();
        entry.setId(info.getId());
        entry.setData(data);

        return entry;
    }

    private String getCurrentDataVersion(FormsStoreEntryInfo info) {
        int revision = info.getRevision();
        List<String> dataVersions = info.getDataVersions();

        return dataVersions.get(revision - 1);
    }

    @Override
    public FormsStoreEntryInfo retrieveEntryInfo(String entryId) {
        Objects.requireNonNull(entryId, "entryId must not be null!");

        return read(FormsStoreEntryInfo.class, entryId);
    }

    @Override
    public FormsStoreEntryInfo createEntryInfo(String entryId, FormsEntryHeader header) {
        Objects.requireNonNull(entryId, "entryId must not be null!");
        Objects.requireNonNull(header, "header must not be null!");

        if (logger.isDebugEnabled())
            logger.debug("Creating a new EntryInfo for id '{}' and header {}", entryId, header);

        String formDefinitionId = header.getFormDefinition();
        ProcessingStages stages = retrieveStages(formDefinitionId);
        String stage = stages.firstName();

        if (logger.isDebugEnabled()) logger.debug("Initial stage: {}", stage);

        List<String> dataVersions = new ArrayList<>();
        dataVersions.add(entryId);

        if (logger.isDebugEnabled()) logger.debug("DataVersions: {}", dataVersions);

        FormsStoreEntryInfo info = new FormsStoreEntryInfo();
        info.setId(entryId);
        info.setRevision(1);
        info.setDataVersions(dataVersions);
        info.setStage(stage);
        info.setHeader(header);
        info.setCreationTimestamp(System.currentTimeMillis());

        if (logger.isDebugEnabled()) logger.debug("Creating entry info {}", info);

        write(info, entryId);

        return info;
    }

    @Override
    public void updateEntryInfo(FormsStoreEntryInfo info) {
        Objects.requireNonNull(info, "info must not be null!");

        if (logger.isDebugEnabled()) logger.debug("Updating entry info {}", info);

        write(info, info.getId());
    }

    @Override
    public void updateProcessingState(FormsStoreEntryInfo info, ProcessingState state) {
        Objects.requireNonNull(info, "info must not be null!");

        FormsDataProcessingRecord processingRecord = info.getProcessingRecord();
        if (processingRecord == null) {
            processingRecord = new FormsDataProcessingRecord();
            info.setProcessingRecord(processingRecord);
        }

        processingRecord.setState(state);

        updateEntryInfo(info);
    }

    @Override
    public Collection<FormsStoreEntryInfo> findEntryInfosByProcessingState(ProcessingState state) {
        Collection<String> allIds = listEntryIds();
        if (allIds == null || allIds.isEmpty()) {
            return Collections.emptySet();
        }

        Collection<FormsStoreEntryInfo> matchingIds = new HashSet<>();

        for (String id : allIds) {
            FormsStoreEntryInfo entryInfo = retrieveEntryInfo(id);
            FormsDataProcessingRecord processingRecord = entryInfo.getProcessingRecord();
            if (processingRecord == null) {
                if (state == null) {
                    matchingIds.add(entryInfo);
                }
            } else {
                if (processingRecord.getState() == state) {
                    matchingIds.add(entryInfo);
                }
            }
        }

        return matchingIds;
    }

    @Override
    public Set<AttachmentDescriptor> getAttachmentDescriptors(String entryId) {
        File directory = new File(getBaseDirectory() + "/" + resolveBasePath(entryId) + "/" + DIR_ATTACHMENTS);
        if (!directory.exists()) {
            return Collections.emptySet();
        }

        Set<AttachmentDescriptor> result = new HashSet<>();
        String[] fileNames = directory.list((dir, name) -> dir.equals(directory));
        if (fileNames != null && fileNames.length > 0) {
            for (String fileName : fileNames) {
                String attachmentName = fileName.substring(0, fileName.lastIndexOf("."));
                String mimeType = MimeTypes.resolveMimeType(fileName);

                AttachmentDescriptor descriptor = new AttachmentDescriptor();
                descriptor.setName(attachmentName);
                descriptor.setFilename(fileName);
                descriptor.setMimeType(mimeType);

                result.add(descriptor);
            }
        }

        return result;
    }

    @Override
    public AttachmentDescriptor getAttachmentDescriptor(String entryId, String name, String mimeType) {
        File directory = new File(getBaseDirectory() + "/" + resolveBasePath(entryId) + "/" + DIR_ATTACHMENTS);
        if (!directory.exists()) {
            return null;
        }

        String fileExtension = MimeTypes.resolveFileExtension(mimeType);
        String attachmentFile = name + "." + fileExtension;

        AttachmentDescriptor descriptor = new AttachmentDescriptor();
        descriptor.setMimeType(mimeType);
        descriptor.setFilename(attachmentFile);
        descriptor.setName(name);

        return descriptor;
    }

    @Override
    public void addObjectAttachment(String entryId, String name, Object attachment) {
        write(attachment, entryId, name);
    }

    @Override
    public void addBinaryAttachment(String entryId, String name, String mimeType, byte[] data) {
        File directory = new File(getBaseDirectory() + "/" + resolveBasePath(entryId) + "/" + DIR_ATTACHMENTS);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Unable to create missing attachments directory '" + directory.getAbsolutePath() + "'!");
        }

        String fileExtension = MimeTypes.resolveFileExtension(mimeType);
        String attachmentFile = name + "." + fileExtension;

        File file = new File(directory, attachmentFile);
        try {
            FileUtils.writeByteArrayToFile(file, data);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error writing data to file (" + file.getAbsolutePath() + ")!", ex);
        }
    }

    @Override
    public <T> T getObjectAttachment(String entryId, String name, Class<T> target) {
        return read(target, entryId, name);
    }

    @Override
    public byte[] getBinaryAttachment(String entryId, String name, String mimeType) {
        File directory = new File(getBaseDirectory() + "/" + resolveBasePath(entryId) + "/" + DIR_ATTACHMENTS);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Unable to create missing attachments directory '" + directory.getAbsolutePath() + "'!");
        }

        String fileExtension = MimeTypes.resolveFileExtension(mimeType);
        String attachmentFile = name + "." + fileExtension;

        File file = new File(directory, attachmentFile);
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error reading data to file (" + file.getAbsolutePath() + ")!", ex);
        }
    }

    @Override
    public byte[] getBinaryAttachment(String entryId, AttachmentDescriptor descriptor) {
        File directory = new File(getBaseDirectory() + "/" + resolveBasePath(entryId) + "/" + DIR_ATTACHMENTS);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Unable to create missing attachments directory '" + directory.getAbsolutePath() + "'!");
        }

        File file = new File(directory, descriptor.getFilename());
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error reading data to file (" + file.getAbsolutePath() + ")!", ex);
        }
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
    public List<String> listDefinitionIds() {
        List<String> result = new ArrayList<>();

        String[] files = definitionsBaseDir.list((dir, name) -> definitionsBaseDir.equals(dir) && name.endsWith(".json"));
        if (files != null && files.length > 0) {
            for (String file : files) {
                result.add(file.substring(0, file.indexOf(".json")));
            }
        }

        return result;
    }

    @Override
    public ProcessingStages retrieveStages(String formDefinitionId) {
        FormDefinition definition = retrieveDefinitionInternal(formDefinitionId);
        if (definition == null) {
            return new ProcessingStages(null);
        }

        return new ProcessingStages(definition.getStages());
    }

    @Override
    public FormDefinition retrieveDefinition(String formDefinitionId, String stageName) {
        FormDefinition definition = retrieveDefinitionInternal(formDefinitionId);

        if (definition == null) {
            return null;
        }

        Map<String, FieldDefinition> fields = definition.getFields();
        if (fields == null || fields.isEmpty()) {
            return definition;
        }

        Map<String, FieldDefinition> fixedFields = new HashMap<>();

        for (Map.Entry<String, FieldDefinition> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            FieldDefinition field = entry.getValue();

            int pos = fieldName.indexOf("@");
            if (pos < 0) {
                fixedFields.put(fieldName, field);
                continue;
            }

            String name = fieldName.substring(0, pos);
            String fieldStageName = fieldName.substring(pos + 1);
            if (stageName.equals(fieldStageName)) {
                fixedFields.put(name, field);
                if (logger.isDebugEnabled())
                    logger.debug("Attaching field '{}' corresponding to stage '{}'", name, stageName);
            } else {
                if (logger.isDebugEnabled())
                    logger.debug("Skipping field '{}', corresponding to another stage '{}'", fieldName, fieldStageName);
            }
        }

        definition.setFields(fixedFields);
        return definition;
    }

    private FormDefinition retrieveDefinitionInternal(String formDefinitionId) {
        File defFile = new File(definitionsBaseDir, formDefinitionId + ".json");
        if (!defFile.exists()) {
            if (logger.isErrorEnabled()) logger.error("No form definition found for id '{}'!", formDefinitionId);
            return null;
        }

        FormDefinition formDefinition;
        try {
            formDefinition = getObjectMapper().readValue(defFile, FormDefinition.class);
        } catch (IOException ex) {
            if (logger.isErrorEnabled())
                logger.error("Unable to read form definition file '{}'!", defFile.getAbsolutePath(), ex);
            return null;
        }

        return formDefinition;
    }

//    @Override
//    public void createDefinition(FormDefinition formDefinition) {
//        String formDefinitionId = formDefinition.getForm();
//
//        File defFile = new File(definitionsBaseDir, formDefinitionId + ".json");
//        if (defFile.exists()) {
//            throw new IllegalArgumentException("Unable to create form definition '" + formDefinitionId + "', because it's already exists!");
//        }
//
//        try {
//            getObjectMapper().writeValue(defFile, formDefinition);
//        } catch (IOException ex) {
//            throw new IllegalStateException("Error writing form definition file '" + defFile.getAbsolutePath() + "'!", ex);
//        }
//    }
//
//    @Override
//    public void updateDefinition(FormDefinition formDefinition) {
//        String formDefinitionId = formDefinition.getForm();
//
//        File defFile = new File(definitionsBaseDir, formDefinitionId + ".json");
//        if (!defFile.exists()) {
//            throw new IllegalArgumentException("Unable to update form definition '" + formDefinitionId + "', because it doesn't exist!");
//        }
//
//        try {
//            getObjectMapper().writeValue(defFile, formDefinition);
//        } catch (IOException ex) {
//            throw new IllegalStateException("Error writing form definition file '" + defFile.getAbsolutePath() + "'!", ex);
//        }
//    }
//
//    @Override
//    public void deleteDefinition(String formDefinitionId) {
//        File defFile = new File(definitionsBaseDir, formDefinitionId + ".json");
//        if (!defFile.exists()) {
//            throw new IllegalArgumentException("Unable to delete form definition '" + formDefinitionId + "', because it doesn't exist!");
//        }
//
//        if (!defFile.delete()) {
//            throw new IllegalStateException("Error deleting form definition file '" + defFile.getAbsolutePath() + "'!");
//        }
//    }

    @Override
    public List<String> listCustomerIds() {
        List<String> result = new ArrayList<>();

        String[] files = customersBaseDir.list((dir, name) -> name.endsWith(".json"));
        if (files != null && files.length > 0) {
            for (String file : files) {
                result.add(file.substring(0, file.indexOf(".json")));
            }
        }

        return result;
    }

    @Override
    public Customer retrieveCustomer(String customerId) {
        File customerFile = new File(customersBaseDir, customerId + ".json");
        if (!customerFile.exists()) {
            if (logger.isErrorEnabled()) logger.error("No form customer found for id '{}'!", customerId);
            return null;
        }

        Customer customer;
        try {
            customer = getObjectMapper().readValue(customerFile, Customer.class);
        } catch (IOException ex) {
            if (logger.isErrorEnabled())
                logger.error("Unable to read form customer file '{}'!", customerFile.getAbsolutePath(), ex);
            return null;
        }

        return customer;
    }

    @Override
    public void createCustomer(Customer customer) {
        String customerId = customer.getId();

        File customerFile = new File(customersBaseDir, customerId + ".json");
        if (customerFile.exists()) {
            throw new IllegalArgumentException("Unable to create form customer '" + customerId + "', because it's already exists!");
        }

        if (logger.isDebugEnabled()) logger.debug("Creating customer {}", customer);

        try {
            getObjectMapper().writeValue(customerFile, customer);
        } catch (IOException ex) {
            throw new IllegalStateException("Error writing form customer file '" + customerFile.getAbsolutePath() + "'!", ex);
        }
    }

    @Override
    public void updateCustomer(Customer customer) {
        String customerId = customer.getId();

        File customerFile = new File(customersBaseDir, customerId + ".json");
        if (!customerFile.exists()) {
            throw new IllegalArgumentException("Unable to update form customer '" + customerId + "', because it doesn't exist!");
        }

        if (logger.isDebugEnabled()) logger.debug("Updating customer {}", customer);

        try {
            getObjectMapper().writeValue(customerFile, customer);
        } catch (IOException ex) {
            throw new IllegalStateException("Error writing form customer file '" + customerFile.getAbsolutePath() + "'!", ex);
        }
    }

    @Override
    public void deleteCustomer(String customerId) {
        File customerFile = new File(customersBaseDir, customerId + ".json");
        if (!customerFile.exists()) {
            throw new IllegalArgumentException("Unable to delete form customer '" + customerId + "', because it doesn't exist!");
        }

        if (!customerFile.delete()) {
            throw new IllegalStateException("Error deleting form customer file '" + customerFile.getAbsolutePath() + "'!");
        }
    }

    @Override
    protected <T> File getFile(Class<T> target, String id, String mimeType, String... path) {
        if (FormsStoreEntryInfo.class.isAssignableFrom(target)) {
            // info
            File directory = new File(getBaseDirectory(), resolveBasePath(id));
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IllegalStateException("Unable to create missing info directory '" + directory.getAbsolutePath() + "'!");
            }

            String targetFile = "info.json";
            return new File(directory, targetFile);
        }

        // attachments
        File directory = new File(getBaseDirectory() + "/" + resolveBasePath(id) + "/" + DIR_ATTACHMENTS);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Unable to create missing attachments directory '" + directory.getAbsolutePath() + "'!");
        }

        String fileExtension = MimeTypes.resolveFileExtension(mimeType);
        String attachmentFile = path[0] + "." + fileExtension;
        return new File(directory, attachmentFile);
    }
}

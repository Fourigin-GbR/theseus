package com.fourigin.argo.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.definition.FormDefinition;
import com.fourigin.argo.forms.models.Attachment;
import com.fourigin.argo.forms.models.FormsDataProcessingState;
import com.fourigin.argo.forms.models.FormsEntryHeader;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingHistoryRecord;
import com.fourigin.argo.forms.models.ProcessingState;
import com.fourigin.utilities.core.JsonFileBasedRepository;
import com.fourigin.utilities.core.MimeTypes;
import de.huxhorn.sulky.blobs.AmbiguousIdException;
import de.huxhorn.sulky.blobs.impl.BlobRepositoryImpl;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class JsonFilesBasedFormsRepository extends JsonFileBasedRepository implements FormsStoreRepository, FormDefinitionRepository, CustomerRepository {
    private BlobRepositoryImpl blobRepository;

    private File definitionsBaseDir;

    private File customersBaseDir;

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

    @Override
    public Collection<String> listEntryIds() {
        return blobRepository.idSet();
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

        entry.setId(entryId);
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

        Map<String, String> data;
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
    public FormsStoreEntryInfo retrieveEntryInfo(String entryId) {
        Objects.requireNonNull(entryId, "entryId must not be null!");

        return read(FormsStoreEntryInfo.class, entryId);
    }

    @Override
    public void createEntryInfo(String entryId, FormsEntryHeader header) {
        Objects.requireNonNull(entryId, "entryId must not be null!");
        Objects.requireNonNull(header, "header must not be null!");

        FormsStoreEntryInfo info = new FormsStoreEntryInfo();
        info.setId(entryId);
        info.setRevision(1L);
        info.setHeader(header);
        info.setCreationTimestamp(System.currentTimeMillis());

        if (logger.isDebugEnabled()) logger.debug("Creating entry info {}", info);

        write(info, entryId);
    }

    @Override
    public void updateEntryInfo(FormsStoreEntryInfo info) {
        Objects.requireNonNull(info, "info must not be null!");

        if (logger.isDebugEnabled()) logger.debug("Updating entry info {}", info);

        write(info, info.getId());
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

        List<ProcessingHistoryRecord> history = formDataProcessingState.getProcessingHistory();
        if (history == null) {
            history = new ArrayList<>();
            formDataProcessingState.setProcessingHistory(history);
        }

        ProcessingHistoryRecord historyEntry = new ProcessingHistoryRecord();
        historyEntry.setTimestamp(System.currentTimeMillis());
        if (context != null) {
            historyEntry.setContext(context);
        }
        history.add(historyEntry);

        write(info, entryId);
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
    public FormDefinition retrieveDefinition(String formDefinitionId) {
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

    @Override
    public void createDefinition(FormDefinition formDefinition) {
        String formDefinitionId = formDefinition.getForm();

        File defFile = new File(definitionsBaseDir, formDefinitionId + ".json");
        if (defFile.exists()) {
            throw new IllegalArgumentException("Unable to create form definition '" + formDefinitionId + "', because it's already exists!");
        }

        try {
            getObjectMapper().writeValue(defFile, formDefinition);
        } catch (IOException ex) {
            throw new IllegalStateException("Error writing form definition file '" + defFile.getAbsolutePath() + "'!", ex);
        }
    }

    @Override
    public void updateDefinition(FormDefinition formDefinition) {
        String formDefinitionId = formDefinition.getForm();

        File defFile = new File(definitionsBaseDir, formDefinitionId + ".json");
        if (!defFile.exists()) {
            throw new IllegalArgumentException("Unable to update form definition '" + formDefinitionId + "', because it doesn't exist!");
        }

        try {
            getObjectMapper().writeValue(defFile, formDefinition);
        } catch (IOException ex) {
            throw new IllegalStateException("Error writing form definition file '" + defFile.getAbsolutePath() + "'!", ex);
        }
    }

    @Override
    public void deleteDefinition(String formDefinitionId) {
        File defFile = new File(definitionsBaseDir, formDefinitionId + ".json");
        if (!defFile.exists()) {
            throw new IllegalArgumentException("Unable to delete form definition '" + formDefinitionId + "', because it doesn't exist!");
        }

        if (!defFile.delete()) {
            throw new IllegalStateException("Error deleting form definition file '" + defFile.getAbsolutePath() + "'!");
        }
    }

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

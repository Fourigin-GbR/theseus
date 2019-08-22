package com.fourigin.argo.forms.dashboard.controller;

import com.fourigin.argo.forms.AttachmentDescriptor;
import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormDefinitionResolver;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.dashboard.CustomerInfo;
import com.fourigin.argo.forms.dashboard.ViewFormRequestInfo;
import com.fourigin.argo.forms.dashboard.ViewProcessingStage;
import com.fourigin.argo.forms.definition.ProcessingStage;
import com.fourigin.argo.forms.definition.ProcessingStages;
import com.fourigin.argo.forms.models.FormsDataProcessingRecord;
import com.fourigin.argo.forms.models.FormsEntryHeader;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingHistoryRecord;
import com.fourigin.argo.forms.models.ProcessingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

@RestController
public class DashboardController {

    private CustomerRepository customerRepository;

    private FormsStoreRepository formsStoreRepository;

    private FormDefinitionResolver formDefinitionResolver;

    private final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    public DashboardController(
            CustomerRepository customerRepository,
            FormsStoreRepository formsStoreRepository,
            FormDefinitionResolver formDefinitionResolver
    ) {
        this.customerRepository = customerRepository;
        this.formsStoreRepository = formsStoreRepository;
        this.formDefinitionResolver = formDefinitionResolver;
    }

    @RequestMapping("/customers")
    public List<CustomerInfo> customers() {
        List<String> ids = customerRepository.listCustomerIds();
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        if (logger.isDebugEnabled()) logger.debug("Existing customer IDs: {}", ids);

        List<CustomerInfo> result = new ArrayList<>();

        for (String id : ids) {
            Customer customer = customerRepository.retrieveCustomer(id);

            if (logger.isDebugEnabled()) logger.debug("Processing customer for id '{}': {}", id, customer);

            CustomerInfo info = new CustomerInfo();
            info.setId(id);
            info.setEntryId(customer.getEntryId());
            info.setFirstName(customer.getFirstname());
            info.setLastName(customer.getLastname());
            info.setEmail(customer.getEmail());
            result.add(info);
        }

        return result;
    }

    @RequestMapping("/requests")
    public List<ViewFormRequestInfo> requests() {
        Collection<String> entryIds = formsStoreRepository.listEntryIds();
        if (entryIds == null || entryIds.isEmpty()) {
            return Collections.emptyList();
        }

        if (logger.isDebugEnabled()) logger.debug("Preparing request-info objects for IDs {}", entryIds);

        List<ViewFormRequestInfo> result = new ArrayList<>();

        for (String entryId : entryIds) {
            FormsStoreEntryInfo entryInfo = formsStoreRepository.retrieveEntryInfo(entryId);
            if (entryInfo == null) {
                if (logger.isWarnEnabled()) logger.warn("No entry info found for id '{}'!", entryId);
                continue;
            }

            try {
                ViewFormRequestInfo requestInfo = convert(entryInfo);
                result.add(requestInfo);
            }
            catch(IllegalArgumentException | IllegalStateException ex) {
                if (logger.isErrorEnabled()) logger.error("Unable to convert entry!", ex);
            }
        }

        return result;
    }

    @RequestMapping("/request")
    public ViewFormRequestInfo request(
            @RequestParam String entryId
    ) {
        FormsStoreEntryInfo entryInfo = formsStoreRepository.retrieveEntryInfo(entryId);
        if (entryInfo == null) {
            throw new IllegalArgumentException("No entry found for id '" + entryId + "'!");
        }

        return convert(entryInfo);
    }

    @RequestMapping("/stages")
    public List<ViewProcessingStage> stages(
            @RequestParam String formId
    ) {
        ProcessingStages stages = formDefinitionResolver.retrieveStages(formId);
        if (stages == null) {
            throw new IllegalArgumentException("No stages found for form definition id '" + formId + "'!");
        }

        List<ViewProcessingStage> result = new ArrayList<>();
        for (ProcessingStage stage : stages.all()) {
            result.add(convert(stage, stages.isEditable(stage.getName())));
        }

        return result;
    }

    private ViewProcessingStage convert(ProcessingStage stage, boolean editable) {
        ViewProcessingStage result = new ViewProcessingStage();

        result.setName(stage.getName());
        result.setActions(stage.getActions());
        result.setEditable(editable);

        return result;
    }

    private ViewFormRequestInfo convert(FormsStoreEntryInfo entryInfo) {
        if (logger.isDebugEnabled()) logger.debug("Converting {}", entryInfo);

        FormsEntryHeader header = entryInfo.getHeader();
        String customerId = header.getCustomerId();
        String entryId = entryInfo.getId();
        String type = header.getFormDefinition();

        Customer customer = customerRepository.retrieveCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("No customer found for id '" + customerId + "'!");
        }

        Set<AttachmentDescriptor> attachments = formsStoreRepository.getAttachmentDescriptors(entryInfo.getId());

        ViewFormRequestInfo info = new ViewFormRequestInfo();
        info.setId(entryId);
        info.setFormDefinition(type);
        info.setStage(entryInfo.getStage());
        info.setBase(header.getBase());
        info.setCustomer(customerId);
        info.setCreationTimestamp(entryInfo.getCreationTimestamp());
        info.setAttachments(attachments);
        info.setProcessingState(entryInfo.getProcessingRecord());

        info.setCustomerName(customer.getFirstname() + ' ' + customer.getLastname());

        Map<String, String> entryData = loadEntryData(entryInfo);
        if (entryData == null) {
            throw new IllegalStateException("No entry data found for '" + entryId + "'!");
        }

        switch (type) {
            case "register-customer":
                info.setRequestData("");
                break;
            case "register-vehicle":
                StringBuilder value = new StringBuilder();
                String existingNameplate = entryData.get("vehicle.existing-nameplate");
                String newNameplateType = entryData.get("vehicle.new-nameplate");
                String newNameplateCustomerRequirements = entryData.get("vehicle.new-nameplate/nameplate-customer-requirements");
                String newNameplateCustomerReservation = entryData.get("vehicle.new-nameplate/nameplate-customer-reservation");

                if (existingNameplate != null && existingNameplate.isEmpty()) {
                    value.append(existingNameplate);
                } else {
                    value.append('-');
                }
                value.append(" / ");
                switch (newNameplateType) {
                    case "portal":
                        value.append(newNameplateCustomerRequirements).append(" (Wunsch)");
                        break;
                    case "self":
                        value.append(newNameplateCustomerReservation).append(" (reserviert)");
                        break;
                    case "random":
                        value.append('-');
                        break;
                    default:
                        throw new IllegalStateException("Unsupported nameplate registration type '" + newNameplateType + "'!");
                }

                info.setRequestData(value.toString());
                break;
            default:
                throw new IllegalStateException("Unsupported form definition type '" + type + "'!");
        }

        return info;
    }

    @RequestMapping("/data")
    public SortedMap<String, String> data(@RequestParam String entryId) {
        Objects.requireNonNull(entryId, "entryId must not bei null!");

        FormsStoreEntryInfo info = formsStoreRepository.retrieveEntryInfo(entryId);
        return loadEntryData(info);
    }

    private SortedMap<String, String> loadEntryData(FormsStoreEntryInfo info) {
        FormsStoreEntry entry = formsStoreRepository.retrieveEntry(info);
        if (entry == null) {
            throw new IllegalArgumentException("No entry found for id '" + info.getId() + "'!");
        }

        Map<String, String> data = entry.getData();
        if (data == null) {
            return null;
        }

        return new TreeMap<>(data);
    }

    @RequestMapping("/attachment")
    public void attachment(
            @RequestParam String entryId,
            @RequestParam String attachmentName,
            @RequestParam String mimeType,
            HttpServletResponse response
    ) {
        Objects.requireNonNull(entryId, "entryId must not bei null!");
        Objects.requireNonNull(attachmentName, "attachmentName must not bei null!");
        Objects.requireNonNull(mimeType, "mimeType must not bei null!");

        AttachmentDescriptor descriptor = formsStoreRepository.getAttachmentDescriptor(entryId, attachmentName, mimeType);
        byte[] data = formsStoreRepository.getBinaryAttachment(entryId, descriptor);

        response.setContentType(mimeType);
        response.setContentLengthLong(data.length);
        response.addHeader("Content-Disposition", "attachment; filename=" + descriptor.getFilename());

        try (OutputStream responseOutputStream = response.getOutputStream()) {
            responseOutputStream.write(data);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to deliver attachment '" + attachmentName + "'!", ex);
        }
    }

    @RequestMapping("/delete-customer")
    public void deleteCustomer(
            @RequestParam("customerId") String customerEntryId
    ) {
        Objects.requireNonNull(customerEntryId, "customerEntryId must not bei null!");

        Map<String, String> entryIds = customerRepository.listCustomerEntryIds();

        if (!entryIds.containsKey(customerEntryId)) {
            throw new IllegalArgumentException("No customer found for entryId '" + customerEntryId + "'!");
        }

        String customerId = entryIds.get(customerEntryId);
        if (logger.isDebugEnabled()) logger.debug("Deleting customer {}", customerId);
        customerRepository.deleteCustomer(customerId);

        Collection<FormsStoreEntryInfo> customerEntries = formsStoreRepository.findEntryInfosByCustomerId(customerId);
        for (FormsStoreEntryInfo customerEntry : customerEntries) {
            if (logger.isDebugEnabled()) logger.debug("Deleting also customer's entry {}", customerEntry.getId());
            formsStoreRepository.deleteEntry(customerEntry);
        }
    }

    @RequestMapping("/change-state")
    public void changeState(
            @RequestParam String customerId,
            @RequestParam String entryId,
            @RequestParam ProcessingState processingState,
            @RequestParam(required = false) String comment
    ) {
        Objects.requireNonNull(customerId, "customerId must not bei null!");
        Objects.requireNonNull(entryId, "entryId must not bei null!");
        Objects.requireNonNull(processingState, "state must not bei null!");

        FormsStoreEntryInfo info = formsStoreRepository.retrieveEntryInfo(entryId);
        FormsDataProcessingRecord record = info.getProcessingRecord();

        boolean changed = false;
        if (comment != null && !comment.isEmpty()) {
            record.setCurrentStatusMessage(comment);

            record.addHistoryRecord(ProcessingHistoryRecord.KEY_MESSAGE, comment);

            changed = true;
        } else {
            record.setCurrentStatusMessage(null);
        }

        if (record.getState() != processingState) {
            record.setState(processingState);

            changed = true;
        }

        if (changed) {
            formsStoreRepository.updateEntryInfo(info);
        }
    }
}

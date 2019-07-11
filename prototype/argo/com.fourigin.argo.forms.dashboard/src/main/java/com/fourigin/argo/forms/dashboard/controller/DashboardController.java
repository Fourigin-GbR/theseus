package com.fourigin.argo.forms.dashboard.controller;

import com.fourigin.argo.forms.AttachmentDescriptor;
import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.dashboard.CustomerInfo;
import com.fourigin.argo.forms.dashboard.FormRequestInfo;
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

    private final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    public DashboardController(CustomerRepository customerRepository, FormsStoreRepository formsStoreRepository) {
        this.customerRepository = customerRepository;
        this.formsStoreRepository = formsStoreRepository;
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
            info.setFirstName(customer.getFirstname());
            info.setLastName(customer.getLastname());
            info.setEmail(customer.getEmail());
            result.add(info);
        }

        return result;
    }

    @RequestMapping("/requests")
    public List<FormRequestInfo> requests() {
        Collection<String> entryIds = formsStoreRepository.listEntryIds();
        if (entryIds == null || entryIds.isEmpty()) {
            return Collections.emptyList();
        }

        if (logger.isDebugEnabled()) logger.debug("Preparing request-info objects for IDs {}", entryIds);

        List<FormRequestInfo> result = new ArrayList<>();

        for (String entryId : entryIds) {
            FormsStoreEntryInfo entryInfo = formsStoreRepository.retrieveEntryInfo(entryId);
            result.add(convertEntry(entryInfo));
        }

        return result;
    }

    @RequestMapping("/request")
    public FormRequestInfo request(
            @RequestParam String entryId
    ) {
        FormsStoreEntryInfo entryInfo = formsStoreRepository.retrieveEntryInfo(entryId);
        if (entryInfo == null) {
            throw new IllegalArgumentException("No entry found for id '" + entryId + "'!");
        }

        return convertEntry(entryInfo);
    }

    private FormRequestInfo convertEntry(FormsStoreEntryInfo entryInfo) {
        FormsEntryHeader header = entryInfo.getHeader();
        String customerId = header.getCustomer();
        String entryId = entryInfo.getId();
        String type = header.getFormDefinition();

        Set<AttachmentDescriptor> attachments = formsStoreRepository.getAttachmentDescriptors(entryInfo.getId());

        FormRequestInfo info = new FormRequestInfo();
        info.setId(entryId);
        info.setFormDefinition(type);
        info.setBase(header.getBase());
        info.setCustomer(customerId);
        info.setCreationTimestamp(entryInfo.getCreationTimestamp());
        info.setAttachments(attachments);
        info.setProcessingState(entryInfo.getProcessingRecord());

        Customer customer = customerRepository.retrieveCustomer(customerId);
        info.setCustomerName(customer.getFirstname() + ' ' + customer.getLastname());

        Map<String, String> entryData = data(entryId);

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
    public SortedMap<String, String> data(
            @RequestParam String entryId
    ) {
        Objects.requireNonNull(entryId, "entryId must not bei null!");

        FormsStoreEntryInfo info = formsStoreRepository.retrieveEntryInfo(entryId);
        FormsStoreEntry entry = formsStoreRepository.retrieveEntry(info);
        if (entry == null) {
            throw new IllegalArgumentException("No entry found for id '" + entryId + "'!");
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
            @RequestParam String customerId
    ) {
        Objects.requireNonNull(customerId, "customerId must not bei null!");

        List<String> allCustomers = customerRepository.listCustomerIds();
        if (!allCustomers.contains(customerId)) {
            throw new IllegalArgumentException("No customer found for id '" + customerId + "'!");
        }

        customerRepository.deleteCustomer(customerId);
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

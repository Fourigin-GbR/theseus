package com.fourigin.argo.forms.dashboard.controller;

import com.fourigin.argo.forms.AttachmentDescriptor;
import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.dashboard.CustomerInfo;
import com.fourigin.argo.forms.dashboard.FormRequestInfo;
import com.fourigin.argo.forms.models.FormsEntryHeader;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
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
import java.util.Set;

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

        List<FormRequestInfo> result = new ArrayList<>();

        for (String entryId : entryIds) {
            FormsStoreEntry entry = formsStoreRepository.retrieveEntry(entryId);
            Map<String, String> entryData = entry.getData();
            String customerId = entryData.get("customer.id"); // TODO: replace with correct customer/client handling!

            FormsStoreEntryInfo entryInfo = formsStoreRepository.retrieveEntryInfo(entryId);
            FormsEntryHeader header = entryInfo.getHeader();

            Set<AttachmentDescriptor> attachments = formsStoreRepository.getAttachmentDescriptors(entryId);

            FormRequestInfo info = new FormRequestInfo();
            info.setId(entryInfo.getId());
            info.setFormDefinition(header.getFormDefinition());
            info.setBase(header.getBase());
            info.setCustomer(customerId);
            info.setCreationTimestamp(entryInfo.getCreationTimestamp());
            info.setAttachments(attachments);
            result.add(info);
        }

        return result;
    }

    @RequestMapping("/attachment")
    public void attachment(
        @RequestParam String entryId,
        @RequestParam String attachmentName,
        @RequestParam String mimeType,
        HttpServletResponse response
    ) {
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
}

package com.fourigin.argo.forms.dashboard.controller;

import com.fourigin.argo.forms.AttachmentDescriptor;
import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.dashboard.CustomerInfo;
import com.fourigin.argo.forms.dashboard.FormRequestInfo;
import com.fourigin.argo.forms.models.FormsEntryHeader;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
public class DashboardController {

    private CustomerRepository customerRepository;

    private FormsStoreRepository formsStoreRepository;

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

        List<CustomerInfo> result = new ArrayList<>();

        for (String id : ids) {
            Customer customer = customerRepository.retrieveCustomer(id);

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
            FormsStoreEntryInfo entryInfo = formsStoreRepository.retrieveEntryInfo(entryId);
            FormsEntryHeader header = entryInfo.getHeader();

            Set<AttachmentDescriptor> attachments = formsStoreRepository.getAttachmentDescriptors(entryId);

            FormRequestInfo info = new FormRequestInfo();
            info.setId(entryInfo.getId());
            info.setFormDefinition(header.getFormDefinition());
            info.setBase(header.getBase());
            info.setCustomer(header.getCustomer());
            info.setAttachments(attachments);
            result.add(info);
        }

        return result;
    }
}

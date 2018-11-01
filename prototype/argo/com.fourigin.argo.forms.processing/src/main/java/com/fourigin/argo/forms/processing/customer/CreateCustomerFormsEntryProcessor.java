package com.fourigin.argo.forms.processing.customer;

import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsEntryProcessor;
import com.fourigin.argo.forms.FormsRegistry;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.models.ProcessingHistoryRecord;

public class CreateCustomerFormsEntryProcessor implements FormsEntryProcessor {
    public static final String NAME = "CREATE-CUSTOMER";

    public static final String ATTACHMENT_NAME = "customer";

    private FormsStoreRepository formsStoreRepository;
    private CustomerRepository customerRepository;

    public CreateCustomerFormsEntryProcessor(
        FormsStoreRepository formsStoreRepository,
        CustomerRepository customerRepository
    ) {
        this.formsStoreRepository = formsStoreRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ProcessingHistoryRecord processEntry(String entryId, FormsRegistry registry) {
        Customer customer = formsStoreRepository.getObjectAttachment(entryId, ATTACHMENT_NAME, Customer.class);
        customerRepository.createCustomer(customer);

        ProcessingHistoryRecord record = new ProcessingHistoryRecord();
        record.setTimestamp(System.currentTimeMillis());
        return record;
    }
}

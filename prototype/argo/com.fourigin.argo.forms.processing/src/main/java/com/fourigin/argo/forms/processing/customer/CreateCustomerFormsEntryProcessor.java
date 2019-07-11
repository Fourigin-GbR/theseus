package com.fourigin.argo.forms.processing.customer;

import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsEntryProcessor;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CreateCustomerFormsEntryProcessor implements FormsEntryProcessor {
    public static final String NAME = "CREATE-CUSTOMER";

    public static final String ATTACHMENT_NAME = "customer";

    private FormsStoreRepository formsStoreRepository;
    private CustomerRepository customerRepository;

    private final Logger logger = LoggerFactory.getLogger(CreateCustomerFormsEntryProcessor.class);

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
    public boolean processEntry(String entryId) {
        Customer customer = formsStoreRepository.getObjectAttachment(entryId, ATTACHMENT_NAME, Customer.class);

        // search for the next free id
        int newId = 1;
        String newIdValue = String.valueOf(newId);

        List<String> availableIds = customerRepository.listCustomerIds();
        while(availableIds.contains(newIdValue)){
            newIdValue = String.valueOf(newId++);
        }

        if (logger.isDebugEnabled()) logger.debug("Generated customer id: '{}'", newIdValue);

        customer.setId(newIdValue);

        FormsStoreEntryInfo info = formsStoreRepository.retrieveEntryInfo(entryId);
        info.getHeader().setCustomer(newIdValue);
        formsStoreRepository.updateEntryInfo(info);

        customerRepository.createCustomer(customer);

        return true;
    }
}

package com.fourigin.argo.forms.processing;

import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsEntryProcessor;
import com.fourigin.argo.forms.FormsRegistry;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.CreateCustomerFormsEntryProcessor;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.models.Attachment;
import com.fourigin.argo.forms.models.ProcessingHistoryRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FulfillVehicleRegistrationFormEntryProcessor implements FormsEntryProcessor {
    public static final String NAME = "FULFILL-VEHICLE-REGISTRATION-FORM";

    public static final String FORM_ATTACHMENT_NAME = "register-vehicle-form";

    private FormsStoreRepository formsStoreRepository;
    private CustomerRepository customerRepository;

    private final Logger logger = LoggerFactory.getLogger(FulfillVehicleRegistrationFormEntryProcessor.class);

    public FulfillVehicleRegistrationFormEntryProcessor(FormsStoreRepository formsStoreRepository, CustomerRepository customerRepository) {
        this.formsStoreRepository = formsStoreRepository;
        this.customerRepository = customerRepository;

        if (logger.isDebugEnabled()) logger.debug("Setting up with {} & {}", this.formsStoreRepository, this.customerRepository);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ProcessingHistoryRecord processEntry(String entryId, FormsRegistry registry) {
        Attachment customerAttachment = formsStoreRepository.getAttachment(entryId, CreateCustomerFormsEntryProcessor.ATTACHMENT_NAME);
        Customer customer = (Customer) customerAttachment.getPayload();

        if (logger.isDebugEnabled()) logger.debug("Using customer {} to fulfill the vehicle registration form", customer);

        return null;
    }
}

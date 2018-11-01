package com.fourigin.argo.forms.processing;

import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsEntryProcessor;
import com.fourigin.argo.forms.FormsRegistry;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.models.ProcessingHistoryRecord;
import com.fourigin.argo.forms.models.VehicleRegistration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public abstract class BaseFulfillFormEntryProcessor implements FormsEntryProcessor {
    protected FormsStoreRepository formsStoreRepository;
    protected CustomerRepository customerRepository;
    protected File form;

    private final Logger logger = LoggerFactory.getLogger(BaseFulfillFormEntryProcessor.class);

    protected abstract String getRegistrationAttachmentName();

    protected abstract String getFormAttachmentName();

    protected abstract void fulfillForm(PDDocument pdDocument, VehicleRegistration registration, Customer customer) throws IOException;

    @Override
    public ProcessingHistoryRecord processEntry(String entryId, FormsRegistry registry) {
        VehicleRegistration registration = formsStoreRepository.getObjectAttachment(
            entryId,
            getRegistrationAttachmentName(),
            VehicleRegistration.class
        );
        if (logger.isDebugEnabled())
            logger.debug("Using registration data to fulfill the vehicle registration form: {}", registration);

        Customer customer = customerRepository.retrieveCustomer(registration.getCustomerId());
        if (logger.isDebugEnabled()) logger.debug("Found customer: {}", customer);

        try (PDDocument pdDocument = PDDocument.load(form)) {
            fulfillForm(pdDocument, registration, customer);

            // write fulfilled pdf form
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            pdDocument.save(baos);
//            EncodedPayload payload = new EncodedPayload();
//            payload.setEncodedDataFromBytes(baos.toByteArray());

            formsStoreRepository.addBinaryAttachment(
                entryId,
                getFormAttachmentName(),
                "application/pdf",
                baos.toByteArray()
            );
        } catch (IOException ex) {
            throw new IllegalStateException("Error processing form fulfillment!", ex);
        }

        ProcessingHistoryRecord record = new ProcessingHistoryRecord();
        record.setTimestamp(System.currentTimeMillis());
        return record;
    }
}

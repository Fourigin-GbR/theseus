package com.fourigin.argo.forms.processing;

import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsEntryProcessor;
import com.fourigin.argo.forms.FormsRegistry;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.customer.CustomerAddress;
import com.fourigin.argo.forms.models.EncodedPayload;
import com.fourigin.argo.forms.models.ProcessingHistoryRecord;
import com.fourigin.argo.forms.models.Vehicle;
import com.fourigin.argo.forms.models.VehicleRegistration;
import com.fourigin.argo.forms.models.payment.BankAccount;
import com.fourigin.utilities.pdfbox.PdfDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class FulfillTaxPaymentFormEntryProcessor implements FormsEntryProcessor {
    public static final String NAME = "FULFILL-TAX-PAYMENT-FORM";

    public static final String REGISTRATION_ATTACHMENT_NAME = "vehicle-registration";

    public static final String FORM_ATTACHMENT_NAME = "tax-payment-form";
    
    private FormsStoreRepository formsStoreRepository;
    private CustomerRepository customerRepository;
    private File form;

    private final Logger logger = LoggerFactory.getLogger(FulfillTaxPaymentFormEntryProcessor.class);

    public FulfillTaxPaymentFormEntryProcessor(
        FormsStoreRepository formsStoreRepository,
        CustomerRepository customerRepository,
        File form
    ) {
        this.formsStoreRepository = formsStoreRepository;
        this.customerRepository = customerRepository;
        this.form = form;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ProcessingHistoryRecord processEntry(String entryId, FormsRegistry registry) {
        VehicleRegistration registration = formsStoreRepository.getAttachment(
            entryId,
            REGISTRATION_ATTACHMENT_NAME,
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
            EncodedPayload payload = new EncodedPayload();
            payload.setEncodedDataFromBytes(baos.toByteArray());

            formsStoreRepository.addAttachment(entryId, FORM_ATTACHMENT_NAME, payload);
        } catch (IOException ex) {
            throw new IllegalStateException("Error processing form fulfillment!", ex);
        }

        ProcessingHistoryRecord record = new ProcessingHistoryRecord();
        record.setTimestamp(System.currentTimeMillis());
        return record;
    }

    protected void fulfillForm(PDDocument pdDocument, VehicleRegistration registration, Customer customer) throws IOException {
        PdfDocument doc = new PdfDocument(pdDocument);

        Vehicle vehicle = registration.getVehicle();

        // fist name + last name
        PDTextField nameField = doc.getTextField("Vorname / Nachname Zahler");
        nameField.setValue(customer.getFirstname() + " " + customer.getLastname());

        CustomerAddress address = customer.getMainAddress();

        // street + house number
        PDTextField streetHouseField = doc.getTextField("Straße / Hausnummer Zahler");
        String streetValue = address.getStreet() + " " + address.getHouseNumber();
        if(address.getAdditionalInfo() != null){
            streetValue += " " + address.getAdditionalInfo();
        }
        streetHouseField.setValue(streetValue);

        // zip
        PDTextField zipField = doc.getTextField("PLZ");
        zipField.setValue(address.getZipCode());

        // city
        PDTextField cityField = doc.getTextField("Ort");
        cityField.setValue(address.getCity());

        // country
        PDTextField countryField = doc.getTextField("Land");
        countryField.setValue(address.getCountry());

        BankAccount account = registration.getBankAccountForTaxPayment();

        // iban
        PDTextField ibanField = doc.getTextField("IBAN");
        ibanField.setValue(account.getIban());

        // bic
        PDTextField bicField = doc.getTextField("BIC");
        bicField.setValue(account.getBic());

        // bank name
        PDTextField bankNameField = doc.getTextField("Name der Bank");
        bankNameField.setValue(account.getBankName());

        // bank account holder
        PDTextField accountHolderField = doc.getTextField("Vorname / Nachname Halter");
        accountHolderField.setValue(account.getAccountHolder());

        // nameplate
        PDTextField nameplateField = doc.getTextField("Kennzeichen");
        switch (vehicle.getNewNameplateOption()) {
            case NOT_REGISTERED:
                // TODO: specify the workflow for this! Just leave empty?
                break;
            case TO_REGISTER_BY_PORTAL:
                // TODO: make a reservation!
                nameplateField.setValue(vehicle.getNewNameplateAdditionalInfo());
                break;
            case ALREADY_REGISTERED_BY_CLIENT:
                nameplateField.setValue(vehicle.getNewNameplateAdditionalInfo());
                break;
            default:
                throw new IllegalStateException("Unsupported nameplate option detected: '" + vehicle.getNewNameplateOption() + "'");
        }
    }
}

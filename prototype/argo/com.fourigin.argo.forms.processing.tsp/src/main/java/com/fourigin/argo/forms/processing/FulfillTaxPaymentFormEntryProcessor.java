package com.fourigin.argo.forms.processing;

import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsEntryProcessor;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.customer.Address;
import com.fourigin.argo.forms.models.Vehicle;
import com.fourigin.argo.forms.models.VehicleRegistration;
import com.fourigin.argo.forms.customer.payment.BankAccount;
import com.fourigin.utilities.pdfbox.PdfDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;

public class FulfillTaxPaymentFormEntryProcessor extends BaseFulfillFormEntryProcessor implements FormsEntryProcessor {
    public static final String NAME = "FULFILL-TAX-PAYMENT-FORM";

    public static final String REGISTRATION_ATTACHMENT_NAME = "vehicle-registration";

    public static final String FORM_ATTACHMENT_NAME = "tax-payment-form";

    // form fields (should be externalized)
    public static final String FIRSTNAME_LASTNAME = "Vorname / Nachname Zahler";
    public static final String STREET_HOUSE_NUMBER = "Straße / Hausnummer Zahler";
    public static final String ZIP_CODE = "PLZ";
    public static final String CITY = "Ort";
    public static final String COUNTRY = "Land";
    public static final String BANK_ACCOUNT = "IBAN";
    public static final String BANK_CODE = "BIC";
    public static final String BANK_NAME = "Name der Bank";
    public static final String OWNER_FIRSTNAME_LASTNAME = "Vorname / Nachname Halter";
    public static final String NAMEPLATE = "Kennzeichen";

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
    protected String getRegistrationAttachmentName() {
        return REGISTRATION_ATTACHMENT_NAME;
    }

    @Override
    protected String getFormAttachmentName() {
        return FORM_ATTACHMENT_NAME;
    }

    protected void fulfillForm(PDDocument pdDocument, VehicleRegistration registration, Customer customer) throws IOException {
        PdfDocument doc = new PdfDocument(pdDocument);

        Vehicle vehicle = registration.getVehicle();
        BankAccount account = registration.getBankAccountForTaxPayment();

        // fist name + last name
        PDTextField nameField = doc.getTextField(FIRSTNAME_LASTNAME);
        nameField.setValue(account.getAccountHolder());

        Address address = account.getAccountHolderAddress();

        // street + house number
        PDTextField streetHouseField = doc.getTextField(STREET_HOUSE_NUMBER);
        String streetValue = address.getStreet() + " " + address.getHouseNumber();
        if(address.getAdditionalInfo() != null){
            streetValue += " " + address.getAdditionalInfo();
        }
        streetHouseField.setValue(streetValue);

        // zip
        PDTextField zipField = doc.getTextField(ZIP_CODE);
        zipField.setValue(address.getZipCode());

        // city
        PDTextField cityField = doc.getTextField(CITY);
        cityField.setValue(address.getCity());

        // country
        PDTextField countryField = doc.getTextField(COUNTRY);
        countryField.setValue(address.getCountry());

        // iban
        PDTextField ibanField = doc.getTextField(BANK_ACCOUNT);
        ibanField.setValue(account.getIban());

        // bic
        PDTextField bicField = doc.getTextField(BANK_CODE);
        bicField.setValue(account.getBic());

        // bank name
        PDTextField bankNameField = doc.getTextField(BANK_NAME);
        bankNameField.setValue(account.getBankName());

        // bank account holder
        PDTextField accountHolderField = doc.getTextField(OWNER_FIRSTNAME_LASTNAME);
        accountHolderField.setValue(customer.getFirstname() + " " + customer.getLastname());

        // nameplate
        PDTextField nameplateField = doc.getTextField(NAMEPLATE);
        String nameplateValue = resolveNameplateToUse(vehicle);
        if(nameplateValue != null){
            nameplateField.setValue(nameplateValue);
        }
    }
}

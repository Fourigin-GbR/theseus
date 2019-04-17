package com.fourigin.argo.forms.processing;

import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsEntryProcessor;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.Address;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.customer.payment.BankAccount;
import com.fourigin.argo.forms.models.HandoverOption;
import com.fourigin.argo.forms.models.NameplateTypeOption;
import com.fourigin.argo.forms.models.Vehicle;
import com.fourigin.argo.forms.models.VehicleRegistration;
import com.fourigin.utilities.pdfbox.PdfDocument;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FulfillInternalCardFormEntryProcessor extends BaseFulfillFormEntryProcessor implements FormsEntryProcessor {
    public static final String NAME = "FULFILL-INTERNAL_CARD";

    public static final String REGISTRATION_ATTACHMENT_NAME = "vehicle-registration";

    public static final String FORM_ATTACHMENT_NAME = "registration-card";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    public FulfillInternalCardFormEntryProcessor(
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

        String id = registration.getId();
        if (id != null && !id.isEmpty()) {
            PDTextField idField = doc.getTextField("Auftrag");
            int len = id.length();
            String visibleId = id.substring(0, 10) + "..." + id.substring(len - 10, len);
            idField.setValue(visibleId.toUpperCase(Locale.US));
        }

        // old nameplate
        String previousNameplate = vehicle.getPreviousNameplate();
        if (previousNameplate != null) {
            PDTextField oldNameplateField = doc.getTextField("Amtliches Kennzeichen");
            oldNameplateField.setValue(previousNameplate);
        }

        // new nameplate
        PDTextField newNameplateField = doc.getTextField("Neues Kennzeichen");
        PDTextField headerNameplateField = doc.getTextField("Kennzeichen");
        String nameplateValue = resolveNameplateToUse(vehicle);
        if (nameplateValue != null) {
            newNameplateField.setValue(nameplateValue);
            headerNameplateField.setValue(nameplateValue);
        }

        // date
        Date now = new Date();
        PDTextField dateField = doc.getTextField("Datum");
        dateField.setValue(DATE_FORMAT.format(now));

        // person
        PDTextField titleField = doc.getTextField("Anrede");
        titleField.setValue(resolveTitle(customer.getGender()));

        // last name
        PDTextField lastNameField = doc.getTextField("Nachname");
        lastNameField.setValue(customer.getLastname());

        // first name
        PDTextField firstNameField = doc.getTextField("Vorname");
        firstNameField.setValue(customer.getFirstname());

        // birthday
        PDTextField birthdayField = doc.getTextField("Geburtsdatum");
        if (customer.getBirthdate() != null) {
            birthdayField.setValue(DATE_FORMAT.format(customer.getBirthdate()));
        }

        // city of born
        PDTextField cityOfBornField = doc.getTextField("Geburtsort");
        cityOfBornField.setValue(customer.getCityOfBorn());

        // address
        Address mainAddress = customer.getMainAddress();

        PDTextField cityField = doc.getTextField("Postleitzahl");
        cityField.setValue(mainAddress.getZipCode());

        PDTextField ortField = doc.getTextField("Ort");
        ortField.setValue(mainAddress.getCity());

        PDTextField addressField = doc.getTextField("Straße");
        addressField.setValue(mainAddress.getStreet() + " " + mainAddress.getHouseNumber());

        // vehicle id
        PDTextField vehicleIdField = doc.getTextField("Identnummer");
        applyTextFieldAppearance(vehicleIdField);
        vehicleIdField.setValue(vehicle.getVehicleIdentNumber());

        PDTextField zbField = doc.getTextField("FzBriefNR");
        applyTextFieldAppearance(zbField);
        zbField.setValue(vehicle.getVehicleId());

        // season nameplates
        if (vehicle.getNameplateTypeOption() == NameplateTypeOption.SEASON) {
            PDTextField seasonStartMonth = doc.getTextField("saison_from");
            seasonStartMonth.setValue(String.valueOf(vehicle.getSeasonStartMonth()));

            PDTextField seasonEndMonth = doc.getTextField("saison_to");
            seasonEndMonth.setValue(String.valueOf(vehicle.getSeasonEndMonth()));
        }

        // insurance-id
        PDTextField insuranceIdField = doc.getTextField("eVBNummer");
        applyTextFieldAppearance(insuranceIdField);
        insuranceIdField.setValue(vehicle.getInsuranceId());

        BankAccount account = registration.getBankAccountForTaxPayment();

        // iban
        PDTextField ibanField = doc.getTextField("IBAN");
        applyTextFieldAppearance(ibanField);
        ibanField.setValue(account.getIban());

        // bic
        PDTextField bicField = doc.getTextField("BIC");
        applyTextFieldAppearance(bicField);
        bicField.setValue(account.getBic());

        // bank name
        PDTextField bankNameField = doc.getTextField("Kreditinstitut");
        applyTextFieldAppearance(bankNameField);
        bankNameField.setValue(account.getBankName());

        PDTextField phoneField = doc.getTextField("Telefonnummer");
        applyTextFieldAppearance(phoneField);
        phoneField.setValue(customer.getPhone());

        PDTextField emailField = doc.getTextField("EMail");
        applyTextFieldAppearance(emailField);
        emailField.setValue(customer.getEmail());

        PDTextField servicePriceField = doc.getTextField("Service");
        servicePriceField.setValue("20,00"); // externalize

        PDTextField nameplatesPriceField = doc.getTextField("Schilder");
        nameplatesPriceField.setValue("20,00");  // externalize

        HandoverOption handoverOption = registration.getHandoverOption();
        switch (handoverOption) {
            case OFFICE_DELIVERING:
                PDCheckBox handoverOfficeDeliveringCheckbox = doc.getCheckboxField("Abholung");
                handoverOfficeDeliveringCheckbox.check();
                break;
            case PICKUP:
                PDCheckBox handoverPickupCheckbox = doc.getCheckboxField("Lieferung");
                handoverPickupCheckbox.check();
                break;
            default:
                throw new IllegalStateException("Unsupported handover option found '" + handoverOption + "'!");
        }

        doc.getCheckboxField("TSP").unCheck();
        doc.getCheckboxField("NICHT TSP").unCheck();
        doc.getCheckboxField("versandt").unCheck();
        doc.getCheckboxField("nicht_versandt").unCheck();
    }

    private void applyTextFieldAppearance(PDTextField textField){
        COSDictionary dict = textField.getCOSObject();
        COSString defaultAppearance = (COSString) dict.getDictionaryObject(COSName.DA);
        if (defaultAppearance != null) {
            dict.setString(COSName.DA, "/Helv 9 Tf 0 g");
        }
    }
}

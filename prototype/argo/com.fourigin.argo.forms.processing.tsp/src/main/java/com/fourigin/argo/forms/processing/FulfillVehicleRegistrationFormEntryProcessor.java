package com.fourigin.argo.forms.processing;

import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsEntryProcessor;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.customer.Address;
import com.fourigin.argo.forms.models.NameplateTypeOption;
import com.fourigin.argo.forms.models.Vehicle;
import com.fourigin.argo.forms.models.VehicleRegistration;
import com.fourigin.utilities.pdfbox.PdfDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FulfillVehicleRegistrationFormEntryProcessor extends BaseFulfillFormEntryProcessor implements FormsEntryProcessor {
    public static final String NAME = "FULFILL-VEHICLE-REGISTRATION-FORM";

    public static final String REGISTRATION_ATTACHMENT_NAME = "vehicle-registration";

    public static final String FORM_ATTACHMENT_NAME = "register-vehicle-form";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    public FulfillVehicleRegistrationFormEntryProcessor(
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

        // registration checkbox
        PDCheckBox registrationCheckbox = doc.getCheckboxField("ZulassungWiederzulassung");
        registrationCheckbox.check();

        // old nameplate
        String previousNameplate = vehicle.getPreviousNameplate();
        if (previousNameplate != null) {
            PDTextField oldNameplateField = doc.getTextField("Bisheriges Kennzeichen");
            oldNameplateField.setValue(previousNameplate);
        }

        // new nameplate
        PDTextField newNameplateField = doc.getTextField("Neues Kennzeichen");
        String nameplateValue = resolveNameplateToUse(vehicle);
        if(nameplateValue != null){
            newNameplateField.setValue(nameplateValue);
        }

        // nameplate type / season nameplates
        if (vehicle.getNameplateTypeOption() == NameplateTypeOption.SEASON) {
            PDTextField seasonStartMonth = doc.getTextField("Saison von");
            seasonStartMonth.setValue(String.valueOf(vehicle.getSeasonStartMonth()));

            PDTextField seasonEndMonth = doc.getTextField("Saison bis");
            seasonEndMonth.setValue(String.valueOf(vehicle.getSeasonEndMonth()));
        }

        // person
        PDTextField titleField = doc.getTextField("Anrede");
        switch (customer.getGender()) {
            case MALE:
                titleField.setValue("Herr");
                break;
            case FEMALE:
                titleField.setValue("Frau");
                break;
            default:
                titleField.setValue(customer.getGender().name());
                break;
        }

        // last name
        PDTextField lastNameField = doc.getTextField("Name / Firma");
        lastNameField.setValue(customer.getLastname());

        // first name
        PDTextField firstNameField = doc.getTextField("Vornamen");
        firstNameField.setValue(customer.getFirstname());

        // birth name
        PDTextField birthNameField = doc.getTextField("ggf Geburtsname");
        birthNameField.setValue(customer.getBirthname());

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

        PDTextField cityField = doc.getTextField("PLZ  Ort");
        cityField.setValue(mainAddress.getZipCode() + " " + mainAddress.getCity());

        PDTextField addressField = doc.getTextField("Straße  Hausnummer");
        addressField.setValue(mainAddress.getStreet() + " " + mainAddress.getHouseNumber());

        // vehicle id
        PDTextField vehicleIdField = doc.getTextField("FahrzeugIdentnummer");
        vehicleIdField.setValue(vehicle.getVehicleIdentNumber());

        PDTextField zbField = doc.getTextField("ZB II alt  neu");
        zbField.setValue(vehicle.getVehicleId());

        // insurance-id
        PDTextField insuranceIdField = doc.getTextField("eVBNr");
        insuranceIdField.setValue(vehicle.getInsuranceId());
    }
}

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

    // TODO: replace with LocalDate
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    // form fields (should be externalized)
    public static final String REQUEST_TYPE = "Antragsart";
    public static final String PREVIOUS_NAMEPLATE = "Bisheriges Kennzeichen";
    public static final String NEW_NAMEPLATE = "Neues Kennzeichen";
    public static final String SEASON_NAMEPLATE = "Checkbox Saison";
    public static final String SEASON_START = "Saison von";
    public static final String SEASON_END = "Saison bis";
    public static final String TITLE = "Anrede  Titel";
    public static final String LASTNAME = "Name  Firmenname";
    public static final String FIRSTNAME = "Vornamen";
    public static final String BIRTH_NAME = "Geburtsname";
    public static final String BIRTH_DATE = "Geburtsdatum";
    public static final String BIRTH_LOCATION = "Geburtsort";
    public static final String ZIP_CODE_CITY = "PLZ  Ort";
    public static final String STREET_HOUSE_NUMBER = "Straße  Hausnummer";
    public static final String VEHICLE_ID = "FahrzeugIdentifizierungsnummer";
    public static final String ZB_II = "ZB II";
    public static final String INSURANCE_NUMBER = "eVBNr";

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
        PDCheckBox registrationCheckbox = doc.getCheckboxField(REQUEST_TYPE);
        registrationCheckbox.setValue("Auswahl1");
//        registrationCheckbox.check();

        // old nameplate
        String previousNameplate = vehicle.getPreviousNameplate();
        if (previousNameplate != null) {
            PDTextField oldNameplateField = doc.getTextField(PREVIOUS_NAMEPLATE);
            oldNameplateField.setValue(previousNameplate);
        }

        // new nameplate
        PDTextField newNameplateField = doc.getTextField(NEW_NAMEPLATE);
        String nameplateValue = resolveNameplateToUse(vehicle);
        if(nameplateValue != null){
            newNameplateField.setValue(nameplateValue);
        }

        // nameplate type / season nameplates
        if (vehicle.getNameplateTypeOption() == NameplateTypeOption.SEASON) {
            PDCheckBox seasonCheckbox = doc.getCheckboxField(SEASON_NAMEPLATE);
            seasonCheckbox.check();

            PDTextField seasonStartMonth = doc.getTextField(SEASON_START);
            seasonStartMonth.setValue(String.valueOf(vehicle.getSeasonStartMonth()));

            PDTextField seasonEndMonth = doc.getTextField(SEASON_END);
            seasonEndMonth.setValue(String.valueOf(vehicle.getSeasonEndMonth()));
        }

        // person
        PDTextField titleField = doc.getTextField(TITLE);
        titleField.setValue(resolveTitle(customer.getGender()));

        // last name
        PDTextField lastNameField = doc.getTextField(LASTNAME);
        lastNameField.setValue(customer.getLastname());

        // first name
        PDTextField firstNameField = doc.getTextField(FIRSTNAME);
        firstNameField.setValue(customer.getFirstname());

        // birth name
        PDTextField birthNameField = doc.getTextField(BIRTH_NAME);
        birthNameField.setValue(customer.getBirthname());

        // birthday
        PDTextField birthdayField = doc.getTextField(BIRTH_DATE);
        if (customer.getBirthdate() != null) {
            birthdayField.setValue(DATE_FORMAT.format(customer.getBirthdate()));
        }

        // city of born
        PDTextField cityOfBornField = doc.getTextField(BIRTH_LOCATION);
        cityOfBornField.setValue(customer.getCityOfBorn());

        // address
        Address mainAddress = customer.getMainAddress();

        PDTextField cityField = doc.getTextField(ZIP_CODE_CITY);
        cityField.setValue(mainAddress.getZipCode() + " " + mainAddress.getCity());

        PDTextField addressField = doc.getTextField(STREET_HOUSE_NUMBER);
        addressField.setValue(mainAddress.getStreet() + " " + mainAddress.getHouseNumber());

        // vehicle id
        PDTextField vehicleIdField = doc.getTextField(VEHICLE_ID);
        vehicleIdField.setValue(vehicle.getVehicleIdentNumber());

        PDTextField zbField = doc.getTextField(ZB_II);
        zbField.setValue(vehicle.getVehicleId());

        // insurance-id
        PDTextField insuranceIdField = doc.getTextField(INSURANCE_NUMBER);
        insuranceIdField.setValue(vehicle.getInsuranceId());
    }
}

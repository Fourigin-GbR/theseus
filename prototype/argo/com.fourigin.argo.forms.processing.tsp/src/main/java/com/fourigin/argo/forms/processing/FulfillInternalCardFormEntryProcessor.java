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

    // TODO: replace with LocalDate
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    // form fields (should be externalized)
    public static final String ORDER = "Auftrag";
    public static final String NAMEPLATE = "Amtliches Kennzeichen";
    public static final String NEW_NAMEPLATE = "Neues Kennzeichen";
    public static final String PREVIOUS_NAMEPLATE = "Kennzeichen";
    public static final String DATE = "Datum";
    public static final String TITLE = "Anrede";
    public static final String FIRSTNAME_LASTNAME = "Auftraggeber";
    public static final String LASTNAME = "Nachname";
    public static final String FIRSTNAME = "Vorname";
    public static final String BIRTH_DATE = "Geburtsdatum";
    public static final String BIRTH_LOCATION = "Geburtsort";
    public static final String ZIP_CODE = "Postleitzahl";
    public static final String CITY = "Ort";
    public static final String STREET_HOUSE_NUMBER = "Straße";
    public static final String VEHICLE_ID = "Identnummer";
    public static final String ZB_NUMBER = "FzBriefNR";
    public static final String SEASON_START = "saison_from";
    public static final String SEASON_END = "saison_to";
    public static final String INSURANCE_NUMBER = "eVBNummer";
    public static final String BANK_ACCOUNT = "IBAN";
    public static final String BANK_CODE = "BIC";
    public static final String BANK_NAME = "Kreditinstitut";
    public static final String PHONE = "Telefonnummer";
    public static final String EMAIL = "EMail";
    public static final String PRICE_SERVICE = "Service";
    public static final String PRICE_NAMEPLATE = "Schilder";
    public static final String DELIVERING_TYPE = "check_delivering";
    public static final String DELIVERING = "Lieferung";
    public static final String PICK_UP = "Abholung";
    public static final String CHECK_PAYED = "check_bezahlt";
    public static final String CHECK_CUSTOMER_CALLED = "check_customer_called";
    public static final String CHECK_CUSTOMER_TYPE = "check_kundentyp";
    public static final String CHECK_SENT = "check_sent";

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
            PDTextField idField = doc.getTextField(ORDER);
//            int len = id.length();
//            String visibleId = id.substring(0, 10) + "..." + id.substring(len - 10, len);
//            idField.setValue(visibleId.toUpperCase(Locale.US));
            idField.setValue(id.toUpperCase(Locale.US));
        }

        // old nameplate
        String previousNameplate = vehicle.getPreviousNameplate();
        if (previousNameplate != null) {
            PDTextField oldNameplateField = doc.getTextField(NAMEPLATE);
            oldNameplateField.setValue(previousNameplate);
        }

        // new nameplate
        PDTextField newNameplateField = doc.getTextField(NEW_NAMEPLATE);
        PDTextField headerNameplateField = doc.getTextField(PREVIOUS_NAMEPLATE);
        String nameplateValue = resolveNameplateToUse(vehicle);
        if (nameplateValue != null) {
            newNameplateField.setValue(nameplateValue);
            headerNameplateField.setValue(nameplateValue);
        }

        // date
        Date now = new Date();
        PDTextField dateField = doc.getTextField(DATE);
        dateField.setValue(DATE_FORMAT.format(now));

        // person
        PDTextField titleField = doc.getTextField(TITLE);
        titleField.setValue(resolveTitle(customer.getGender()));

        // both names
        PDTextField bothNamesField = doc.getTextField(FIRSTNAME_LASTNAME);
        bothNamesField.setValue(customer.getLastname() + ", " + customer.getFirstname());

        // last name
        PDTextField lastNameField = doc.getTextField(LASTNAME);
        lastNameField.setValue(customer.getLastname());

        // first name
        PDTextField firstNameField = doc.getTextField(FIRSTNAME);
        firstNameField.setValue(customer.getFirstname());

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

        PDTextField cityField = doc.getTextField(ZIP_CODE);
        cityField.setValue(mainAddress.getZipCode());

        PDTextField ortField = doc.getTextField(CITY);
        ortField.setValue(mainAddress.getCity());

        PDTextField addressField = doc.getTextField(STREET_HOUSE_NUMBER);
        addressField.setValue(mainAddress.getStreet() + " " + mainAddress.getHouseNumber());

        // vehicle id
        PDTextField vehicleIdField = doc.getTextField(VEHICLE_ID);
        applyTextFieldAppearance(vehicleIdField);
        vehicleIdField.setValue(vehicle.getVehicleIdentNumber());

        PDTextField zbField = doc.getTextField(ZB_NUMBER);
        applyTextFieldAppearance(zbField);
        zbField.setValue(vehicle.getVehicleId());

        // season nameplates
        if (vehicle.getNameplateTypeOption() == NameplateTypeOption.SEASON) {
            PDTextField seasonStartMonth = doc.getTextField(SEASON_START);
            seasonStartMonth.setValue(String.valueOf(vehicle.getSeasonStartMonth()));

            PDTextField seasonEndMonth = doc.getTextField(SEASON_END);
            seasonEndMonth.setValue(String.valueOf(vehicle.getSeasonEndMonth()));
        }

        // insurance-id
        PDTextField insuranceIdField = doc.getTextField(INSURANCE_NUMBER);
        applyTextFieldAppearance(insuranceIdField);
        insuranceIdField.setValue(vehicle.getInsuranceId());

        BankAccount account = registration.getBankAccountForTaxPayment();

        // iban
        PDTextField ibanField = doc.getTextField(BANK_ACCOUNT);
        applyTextFieldAppearance(ibanField);
        ibanField.setValue(account.getIban());

        // bic
        PDTextField bicField = doc.getTextField(BANK_CODE);
        applyTextFieldAppearance(bicField);
        bicField.setValue(account.getBic());

        // bank name
        PDTextField bankNameField = doc.getTextField(BANK_NAME);
        applyTextFieldAppearance(bankNameField);
        bankNameField.setValue(account.getBankName());

        PDTextField phoneField = doc.getTextField(PHONE);
        applyTextFieldAppearance(phoneField);
        phoneField.setValue(customer.getPhone());

        PDTextField emailField = doc.getTextField(EMAIL);
        applyTextFieldAppearance(emailField);
        emailField.setValue(customer.getEmail());

        PDTextField servicePriceField = doc.getTextField(PRICE_SERVICE);
        servicePriceField.setValue("20,00"); // externalize

        PDTextField nameplatesPriceField = doc.getTextField(PRICE_NAMEPLATE);
        nameplatesPriceField.setValue("20,00");  // externalize

        HandoverOption handoverOption = registration.getHandoverOption();
        PDCheckBox handoverCheckbox = doc.getCheckboxField(DELIVERING_TYPE);
        switch (handoverOption) {
            case OFFICE_DELIVERING:
                handoverCheckbox.setValue(DELIVERING);
                break;
            case PICKUP:
                handoverCheckbox.setValue(PICK_UP);
                break;
            default:
                throw new IllegalStateException("Unsupported handover option found '" + handoverOption + "'!");
        }

        doc.getCheckboxField(CHECK_PAYED).unCheck();
        doc.getCheckboxField(CHECK_CUSTOMER_CALLED).unCheck();
        doc.getCheckboxField(CHECK_CUSTOMER_TYPE).unCheck();
        doc.getCheckboxField(CHECK_SENT).unCheck();
    }

    private void applyTextFieldAppearance(PDTextField textField){
        COSDictionary dict = textField.getCOSObject();
        COSString defaultAppearance = (COSString) dict.getDictionaryObject(COSName.DA);
        if (defaultAppearance != null) {
            dict.setString(COSName.DA, "/Helv 9 Tf 0 g");
        }
    }
}

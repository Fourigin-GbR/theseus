package com.fourigin.argo.forms.processing;

import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.FormsEntryProcessor;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.customer.Gender;
import com.fourigin.argo.forms.models.VehicleRegistration;
import com.fourigin.utilities.pdfbox.PdfDocument;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FulfillChecklistFormEntryProcessor extends BaseFulfillFormEntryProcessor implements FormsEntryProcessor {
    public static final String NAME = "FULFILL-CHECKLIST";

    public static final String REGISTRATION_ATTACHMENT_NAME = "vehicle-registration";

    public static final String FORM_ATTACHMENT_NAME = "checklist";

    // TODO: replace with LocalDate
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    // form fields (should be externalized)
    public static final String SALUTATION = "text_salutation";
    public static final String DATE = "text_date";
    public static final String CHECK_PASS = "check_pass";
    public static final String CHECK_BRIEF = "check_brief";
    public static final String CHECK_HU = "check_hu";
    public static final String CHECK_AU = "check_au";
    public static final String CHECK_NAMEPLATE = "check_nameplate";

    public FulfillChecklistFormEntryProcessor(
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

        StringBuilder salutation = new StringBuilder();
        if(customer.getGender() == Gender.MALE){
            salutation.append("Sehr geehrter Herr ");
        }
        else {
            salutation.append("Sehr geehrte Frau ");
        }
        salutation.append(customer.getLastname()).append(",");

        PDTextField salutationField = doc.getTextField(SALUTATION);
        applyTextFieldAppearance(salutationField);
        salutationField.setValue(salutation.toString());

        // date
        Date now = new Date();
        PDTextField dateField = doc.getTextField(DATE);
        applyTextFieldAppearance(dateField);
        dateField.setValue(DATE_FORMAT.format(now));

        doc.getCheckboxField(CHECK_PASS).check();
        doc.getCheckboxField(CHECK_BRIEF).check();
        doc.getCheckboxField(CHECK_HU).check();
        doc.getCheckboxField(CHECK_AU).check();
        doc.getCheckboxField(CHECK_NAMEPLATE).unCheck();
    }

    private void applyTextFieldAppearance(PDTextField textField){
        COSDictionary dict = textField.getCOSObject();
        COSString defaultAppearance = (COSString) dict.getDictionaryObject(COSName.DA);
        if (defaultAppearance != null) {
            dict.setString(COSName.DA, "/Helv 12 Tf 0 g");
        }
    }
}

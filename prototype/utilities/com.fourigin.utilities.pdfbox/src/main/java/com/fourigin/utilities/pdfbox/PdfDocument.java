package com.fourigin.utilities.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

public class PdfDocument {
    private PDAcroForm acroForm;

    private String defaultAppearance = "/Helv 12 Tf 0 g";

    public PdfDocument(PDDocument pdDocument) {
        this.acroForm = pdDocument.getDocumentCatalog().getAcroForm();

        if (acroForm == null) {
            throw new IllegalStateException("No AcroForm found in the document!");
        }

//        this.acroForm.setXFA(null);
//        this.acroForm.setNeedAppearances(true);
    }

    public PDTextField getTextField(String fieldName) {
        PDTextField field = getField(PDTextField.class, fieldName);
        field.setDefaultAppearance(defaultAppearance);

        return field;
    }

    public PDCheckBox getCheckboxField(String fieldName) {
        return getField(PDCheckBox.class, fieldName);
    }

    public <T extends PDField> T getField(Class<T> target, String fieldName) {
        PDField field = acroForm.getField(fieldName);
        if (field == null) {
            throw new IllegalArgumentException("No field found for name '" + fieldName + "'!");
        }

        if (!target.isAssignableFrom(field.getClass())) {
            throw new IllegalStateException("Wrong field type detected for field '" + fieldName + "': expected '" + target.getSimpleName() + "' , but found " + field.getClass().getSimpleName());
        }
        return target.cast(field);
    }

    public void setDefaultAppearance(String defaultAppearance) {
        this.defaultAppearance = defaultAppearance;
    }
}

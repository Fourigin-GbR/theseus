package com.fourigin.argo;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class PdfBoxPlayground {
    private PdfBoxPlayground(){
    }

    public static void main(String[] args) throws IOException {
        String formIn = "/work/fourigin/tsp/form.pdf";
        String formOut = "/work/fourigin/tsp/form_out.pdf";

        try (PDDocument pdfDocument = PDDocument.load(new File(formIn))) {
            // get the document catalog
            PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();

            // as there might not be an AcroForm entry a null check is necessary
            if (acroForm != null) {
                // Retrieve an individual field and set its value.
//                PDTextField text = (PDTextField) acroForm.getField("MyTestText");
//                PDTextField text = (PDTextField) acroForm.getField("Bisheriges Kennzeichen");
//                System.out.println("\t- defaultValue: " + text.getDefaultValue());
//                System.out.println("\t- value: " + text.getValue());
//                System.out.println("\t- valueAsString: " + text.getValueAsString());
//                System.out.println("\t- maxLen: " + text.getMaxLen());
//                System.out.println("\t- alternateFieldName: " + text.getAlternateFieldName());
//                System.out.println("\t- defaultAppearance: " + text.getDefaultAppearance());
//                System.out.println("\t- fieldType: " + text.getFieldType());
//                System.out.println("\t- fullyQualifiedName: " + text.getFullyQualifiedName());
//                System.out.println("\t- mappingName: " + text.getMappingName());
//                System.out.println("\t- partialName: " + text.getPartialName());
//                System.out.println("\t- richTextValue: " + text.getRichTextValue());
//                System.out.println("\t- actions: " + text.getActions());
//                System.out.println("\t- COSObject: " + text.getCOSObject());
//                System.out.println("\t- fieldFlags: " + text.getFieldFlags());
//                System.out.println("\t- q: " + text.getQ());
//
//                List<PDAnnotationWidget> widgets = text.getWidgets();
//                if (widgets != null) {
//                    for (PDAnnotationWidget widget : widgets) {
//                        System.out.println("\t\t- widget.hidden: " + widget.isHidden());
//                        System.out.println("\t\t- widget.noView: " + widget.isNoView());
//                        System.out.println("\t\t- widget.invisible: " + widget.isInvisible());
//                    }
//                }
//
//                text.setValue("BLAH");
//                text.setReadOnly(true);


                List<PDField> fields = acroForm.getFields();
                for (PDField field : fields) {
                    System.out.println("Found field: " + field);

//                    String type = field.getClass().getSimpleName();
//                    switch (type){
//                        case "PDCheckBox":
//                            PDCheckBox checkBox = (PDCheckBox) field;
//                            checkBox.check();
//                            System.out.println("\t- checkbox: " + checkBox);
//                            break;
//                        case "PDTextField":
//                            PDTextField text = (PDTextField) acroForm.getField(field.getFullyQualifiedName());
//
//                            text.setValue("blah");
//                            System.out.println("\t- text: " + text);
//                            break;
//                        default:
//                            System.out.println("\t- unsupported type '" + type + "'");
//                            break;
//                    }
                }

//                PDTextField field = (PDTextField) acroForm.getField("sampleField");
//                field.setValue("Text Entry");
//
//                // If a field is nested within the form tree a fully qualified name
//                // might be provided to access the field.
//                field = (PDTextField) acroForm.getField("fieldsContainer.nestedSampleField");
//                field.setValue("Text Entry");
            }

            // Save and close the filled out form.
            pdfDocument.save(formOut);
        }
    }
}

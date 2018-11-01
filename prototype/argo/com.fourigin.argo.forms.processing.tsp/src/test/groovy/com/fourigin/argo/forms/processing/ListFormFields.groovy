package com.fourigin.argo.forms.processing

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDField

final class ListFormFields {
    private ListFormFields() {
    }

    static void main(String[] args) throws IOException {
        String formIn = "/work/fourigin/tsp/form-sepa-tax.pdf"

        PDDocument pdfDocument = PDDocument.load(new File(formIn))

        try {
            // get the document catalog
            PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm()

            // as there might not be an AcroForm entry a null check is necessary
            if (acroForm != null) {

                List<PDField> fields = acroForm.getFields()
                for (PDField field : fields) {
                    System.out.println("Found field: " + field)
                }
            }
        }
        finally {

        }
    }
}

package com.fourigin.argo.forms.definition;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class FormDefinition implements Serializable {
    private static final long serialVersionUID = 3684763498863264351L;

    private String form;
    private Map<String, String> validationPatterns;
    private Map<String, FieldDefinition> fields;

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public Map<String, String> getValidationPatterns() {
        return validationPatterns;
    }

    public void setValidationPatterns(Map<String, String> validationPatterns) {
        this.validationPatterns = validationPatterns;
    }

    public Map<String, FieldDefinition> getFields() {
        return fields;
    }

    public void setFields(Map<String, FieldDefinition> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormDefinition)) return false;
        FormDefinition that = (FormDefinition) o;
        return Objects.equals(form, that.form) &&
            Objects.equals(validationPatterns, that.validationPatterns) &&
            Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(form, validationPatterns, fields);
    }

    @Override
    public String toString() {
        return "FormDefinition{" +
            "form='" + form + '\'' +
            ", validationPatterns=" + validationPatterns +
            ", fields=" + fields +
            '}';
    }
}

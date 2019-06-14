package com.fourigin.argo.forms.validation;

import com.fourigin.argo.forms.definition.FormDefinition;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FormValidationResult implements Serializable {
    private static final long serialVersionUID = 8089342375430013642L;

    private boolean preValidation;
    private boolean valid;
    private Map<String, FormFieldValidationResult> fields;
    private FormDefinition formDefinition;

    public boolean isPreValidation() {
        return preValidation;
    }

    public void setPreValidation(boolean preValidation) {
        this.preValidation = preValidation;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Map<String, FormFieldValidationResult> getFields() {
        return fields;
    }

    public void setFields(Map<String, FormFieldValidationResult> fields) {
        this.fields = fields;
    }

    public FormDefinition getFormDefinition() {
        return formDefinition;
    }

    public void setFormDefinition(FormDefinition formDefinition) {
        this.formDefinition = formDefinition;
    }

    public FormFieldValidationResult findOrCreateFieldValidationResult(String fieldName) {
        if (fieldName == null) {
            return null;
        }

        if (fields == null) {
            fields = new HashMap<>();
        }

        FormFieldValidationResult result = fields.get(fieldName);
        if (result == null) {
            result = new FormFieldValidationResult();
            fields.put(fieldName, result);
        }

        return result;
    }

    public void addFieldValidationResult(String fieldName, FormFieldValidationResult result) {
        if (fieldName == null && result == null) {
            return;
        }

        if (result == null) {
            if (fields != null) {
                fields.remove(fieldName);

                if (fields.isEmpty()) {
                    fields = null;
                }
            }
        } else {
            if (fields == null) {
                fields = new HashMap<>();
            }

            fields.put(fieldName, result);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormValidationResult)) return false;
        FormValidationResult that = (FormValidationResult) o;
        return preValidation == that.preValidation &&
            valid == that.valid &&
            Objects.equals(formDefinition, that.formDefinition) &&
            Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formDefinition, preValidation, valid, fields);
    }

    @Override
    public String toString() {
        return "FormValidationResult{" +
            "formDefinition='" + formDefinition + '\'' +
            ", preValidation=" + preValidation +
            ", valid=" + valid +
            ", fields=" + fields +
            '}';
    }
}

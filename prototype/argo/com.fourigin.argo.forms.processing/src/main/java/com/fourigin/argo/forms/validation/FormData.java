package com.fourigin.argo.forms.validation;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class FormData implements Serializable {
    private static final long serialVersionUID = 8228830652102994697L;

    private String formId;
    private String formDefinitionId;
    private boolean preValidation;
    private Map<String, String> validateFields;
    private Map<String, String> stateFields;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormDefinitionId() {
        return formDefinitionId;
    }

    public void setFormDefinitionId(String formDefinitionId) {
        this.formDefinitionId = formDefinitionId;
    }

    public boolean isPreValidation() {
        return preValidation;
    }

    public void setPreValidation(boolean preValidation) {
        this.preValidation = preValidation;
    }

    public Map<String, String> getValidateFields() {
        return validateFields;
    }

    public void setValidateFields(Map<String, String> validateFields) {
        this.validateFields = validateFields;
    }

    public Map<String, String> getStateFields() {
        return stateFields;
    }

    public void setStateFields(Map<String, String> stateFields) {
        this.stateFields = stateFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormData)) return false;
        FormData formData = (FormData) o;
        return preValidation == formData.preValidation &&
            Objects.equals(formId, formData.formId) &&
            Objects.equals(formDefinitionId, formData.formDefinitionId) &&
            Objects.equals(validateFields, formData.validateFields) &&
            Objects.equals(stateFields, formData.stateFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formId, formDefinitionId, preValidation, validateFields, stateFields);
    }

    @Override
    public String toString() {
        return "FormData{" +
            "formId='" + formId + '\'' +
            ", formDefinitionId='" + formDefinitionId + '\'' +
            ", preValidation=" + preValidation +
            ", validateFields=" + validateFields +
            ", stateFields=" + stateFields +
            '}';
    }
}

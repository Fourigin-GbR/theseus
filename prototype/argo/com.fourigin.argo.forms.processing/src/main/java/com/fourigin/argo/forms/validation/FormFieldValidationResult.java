package com.fourigin.argo.forms.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FormFieldValidationResult implements Serializable {
    private static final long serialVersionUID = 3989909499667879603L;

    private Object value;
    private List<ValidationMessage> hints;
    private List<ValidationMessage> errorMessages;

    public void addHint(ValidationMessage message){
        if (hints == null) {
            hints = new ArrayList<>();
        }

        hints.add(message);
    }

    public void addErrorMessage(ValidationMessage message){
        if (errorMessages == null) {
            errorMessages = new ArrayList<>();
        }

        errorMessages.add(message);
    }

    public boolean isValid() {
        return errorMessages == null || errorMessages.isEmpty();
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<ValidationMessage> getHints() {
        return hints;
    }

    public void setHints(List<ValidationMessage> hints) {
        this.hints = hints;
    }

    public List<ValidationMessage> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<ValidationMessage> failureReasons) {
        this.errorMessages = failureReasons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormFieldValidationResult)) return false;
        FormFieldValidationResult result = (FormFieldValidationResult) o;
        return Objects.equals(value, result.value) &&
            Objects.equals(hints, result.hints) &&
            Objects.equals(errorMessages, result.errorMessages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, hints, errorMessages);
    }

    @Override
    public String toString() {
        return "FormFieldValidationResult{" +
            "value=" + value +
            ", hints=" + hints +
            ", errorMessages=" + errorMessages +
            '}';
    }
}

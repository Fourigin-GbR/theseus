package com.fourigin.argo.forms.validation;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FormFieldValidationResult implements Serializable {
    private static final long serialVersionUID = 3989909499667879603L;

    private boolean valid;
    private Object value;
    private List<FailureReason> failureReasons;

    public static FormFieldValidationResult success(Object value){
        FormFieldValidationResult result = new FormFieldValidationResult();

        result.setValid(true);
        result.setValue(value);

        return result;
    }

    public static FormFieldValidationResult failure(Object value, FailureReason... reasons){
        FormFieldValidationResult result = new FormFieldValidationResult();

        result.setValid(false);
        result.setValue(value);
        result.setFailureReasons(Arrays.asList(reasons));

        return result;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<FailureReason> getFailureReasons() {
        return failureReasons;
    }

    public void setFailureReasons(List<FailureReason> failureReasons) {
        this.failureReasons = failureReasons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormFieldValidationResult)) return false;
        FormFieldValidationResult that = (FormFieldValidationResult) o;
        return valid == that.valid &&
            Objects.equals(value, that.value) &&
            Objects.equals(failureReasons, that.failureReasons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, value, failureReasons);
    }

    @Override
    public String toString() {
        return "FormFieldValidationResult{" +
            "valid=" + valid +
            ", value=" + value +
            ", failureReasons=" + failureReasons +
            '}';
    }
}

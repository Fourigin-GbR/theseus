package com.fourigin.argo.forms.validation;

public interface FormFieldValidator {
    FailureReason validateField(String fieldName, String fieldValue, Object validatorValue);
}

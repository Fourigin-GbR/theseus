package com.fourigin.argo.forms.validation;

import com.fourigin.argo.forms.definition.FormDefinition;

public interface FormFieldValidator {
    FailureReason validateField(FormDefinition formDefinition, String fieldName, String fieldValue, Object validatorValue);
}

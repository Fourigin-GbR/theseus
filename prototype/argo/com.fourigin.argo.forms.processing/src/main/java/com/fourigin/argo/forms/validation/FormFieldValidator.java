package com.fourigin.argo.forms.validation;

import com.fourigin.argo.forms.definition.FormDefinition;

public interface FormFieldValidator {
    boolean isOptional();
    ValidationMessage validateField(FormDefinition formDefinition, String fieldName, String fieldValue, Object validatorValue);
}

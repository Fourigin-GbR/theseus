package com.fourigin.argo.forms.validation;

import com.fourigin.argo.forms.definition.FormDefinition;

import java.util.Objects;

public class MandatoryFormFieldValidator implements FormFieldValidator {

    public static final String VALIDATION_ERROR_INCOMPATIBLE_VALIDATOR_VALUE = "INCOMPATIBLE_VALIDATOR_VALUE";
    public static final String VALIDATION_ERROR_MISSING_MANDATORY_VALUE = "MISSING_MANDATORY_VALUE";

    private boolean optional;

    public MandatoryFormFieldValidator(boolean optional) {
        this.optional = optional;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public ValidationMessage validateField(FormDefinition formDefinition, String fieldName, String fieldValue, Object validatorValue) {
        Objects.requireNonNull(validatorValue, "validatorValue must not be null!");

        if (!Boolean.class.isAssignableFrom(validatorValue.getClass())) {
            return new ValidationMessage.Builder()
                .withValidator("MandatoryFormFieldValidator")
                .withCode(VALIDATION_ERROR_INCOMPATIBLE_VALIDATOR_VALUE)
                .withArgument(validatorValue.getClass().getName())
                .withArgument(Boolean.class.getName())
                .build();
        }

        Boolean shouldBeMandatory = (Boolean) validatorValue;
        if (shouldBeMandatory && (fieldValue == null || fieldValue.isEmpty())) {
            return new ValidationMessage.Builder()
                .withValidator("MandatoryFormFieldValidator")
                .withCode(VALIDATION_ERROR_MISSING_MANDATORY_VALUE)
                .withArgument(fieldName)
                .build();
        }

        return null;
    }
}

package com.fourigin.argo.forms.validation;

import java.util.Objects;

public class MandatoryFormFieldValidator implements FormFieldValidator {

    public static final String VALIDATION_ERROR_INCOMPATIBLE_VALIDATOR_VALUE = "INCOMPATIBLE_VALIDATOR_VALUE";
    public static final String VALIDATION_ERROR_MISSING_MANDATORY_VALUE = "MISSING_MANDATORY_VALUE";

    @Override
    public FailureReason validateField(String fieldName, String fieldValue, Object validatorValue) {
        Objects.requireNonNull(validatorValue, "validatorValue must not be null!");

        if (!Boolean.class.isAssignableFrom(validatorValue.getClass())) {
            return new FailureReason.Builder()
                .withValidator("MandatoryFormFieldValidator")
                .withCode(VALIDATION_ERROR_INCOMPATIBLE_VALIDATOR_VALUE)
                .withArgument("value", validatorValue.toString())
                .build();
        }

        Boolean shouldBeMandatory = (Boolean) validatorValue;
        if (shouldBeMandatory && (fieldValue == null || fieldValue.isEmpty())) {
            return new FailureReason.Builder()
                .withValidator("MandatoryFormFieldValidator")
                .withCode(VALIDATION_ERROR_MISSING_MANDATORY_VALUE)
                .build();
        }

        return null;
    }
}

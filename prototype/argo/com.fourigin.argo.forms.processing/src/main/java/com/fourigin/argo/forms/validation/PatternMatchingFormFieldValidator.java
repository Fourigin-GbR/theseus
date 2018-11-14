package com.fourigin.argo.forms.validation;

import com.fourigin.argo.forms.definition.FormDefinition;

import java.util.Map;
import java.util.Objects;

public class PatternMatchingFormFieldValidator implements FormFieldValidator {

    public static final String VALIDATION_ERROR_INCOMPATIBLE_VALIDATOR_VALUE = "INCOMPATIBLE_VALIDATOR_VALUE";
    public static final String VALIDATION_ERROR_MISSING_VALUE = "MISSING_VALUE";
    public static final String VALIDATION_ERROR_VALUE_MISMATCH = "VALUE_MISMATCH";
    public static final String VALIDATION_ERROR_UNKNOWN_PATTERN = "UNKNOWN_PATTERN";

    @Override
    public FailureReason validateField(FormDefinition formDefinition, String fieldName, String fieldValue, Object validatorValue) {
        Objects.requireNonNull(validatorValue, "validatorValue must not be null!");

        if (!String.class.isAssignableFrom(validatorValue.getClass())) {
            return new FailureReason.Builder()
                .withValidator("PatternMatchingFormFieldValidator")
                .withCode(VALIDATION_ERROR_INCOMPATIBLE_VALIDATOR_VALUE)
                .withArgument(validatorValue.getClass().getName())
                .withArgument(String.class.getName())
                .build();
        }

        if (fieldValue == null) {
            return new FailureReason.Builder()
                .withValidator("PatternMatchingFormFieldValidator")
                .withCode(VALIDATION_ERROR_MISSING_VALUE)
                .withArgument(fieldName)
                .build();
        }

        if (fieldValue.isEmpty()) {
            // empty values are allowed!
            return null;
        }

        String patternName = (String) validatorValue;
        Map<String, String> patternMapping = formDefinition.getValidationPatterns();
        if (patternMapping == null || !patternMapping.containsKey(patternName)) {
            return new FailureReason.Builder()
                .withValidator("PatternMatchingFormFieldValidator")
                .withCode(VALIDATION_ERROR_UNKNOWN_PATTERN)
                .withArgument(patternName)
                .withArgument(patternMapping == null ? "" : String.valueOf(patternMapping.keySet()))
                .build();
        }

        String pattern = patternMapping.get(patternName);

        if (!fieldValue.matches(pattern)) {
            return new FailureReason.Builder()
                .withValidator("PatternMatchingFormFieldValidator")
                .withCode(VALIDATION_ERROR_VALUE_MISMATCH)
                .withArgument(fieldName)
                .withArgument(fieldValue)
                .withArgument(pattern)
                .build();
        }

        return null;
    }
}

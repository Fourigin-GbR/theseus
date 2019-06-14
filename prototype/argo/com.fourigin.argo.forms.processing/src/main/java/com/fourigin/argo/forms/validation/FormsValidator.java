package com.fourigin.argo.forms.validation;

import com.fourigin.argo.forms.definition.FieldDefinition;
import com.fourigin.argo.forms.definition.FieldDefinitions;
import com.fourigin.argo.forms.definition.FormDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class FormsValidator {

    public static final String VALIDATION_ERROR_MISSING_FIELD_DEFINITION = "MISSING_FIELD_DEFINITION";
    public static final String VALIDATION_ERROR_MISSING_FIELD_VALIDATOR = "MISSING_FIELD_VALIDATOR";
    public static final String VALIDATION_ERROR_MISMATCHED_FIELD_VALUE = "MISMATCHED_FIELD_VALUE";
    private static final Set<String> VALID_CHECK_VALUES;

    private static final Map<String, FormFieldValidator> FIELD_VALIDATORS;

    static {
        FIELD_VALIDATORS = new HashMap<>();
        FIELD_VALIDATORS.put("mandatory", new MandatoryFormFieldValidator(false));
        FIELD_VALIDATORS.put("optional-mandatory", new MandatoryFormFieldValidator(true));
        FIELD_VALIDATORS.put("pattern", new PatternMatchingFormFieldValidator(false));
        FIELD_VALIDATORS.put("optional-pattern", new PatternMatchingFormFieldValidator(true));

        VALID_CHECK_VALUES = new HashSet<>(Arrays.asList(
            "true",
            "false",
            "on",
            "off",
            "yes",
            "no"
        ));
    }

    private FormsValidator() {
    }

    public static FormValidationResult validate(FormDefinition formDefinition, FormData data) {
        Objects.requireNonNull(data, "FormData must not be null!");
        Objects.requireNonNull(formDefinition, "FormDefinition must not be null!");

        final Logger logger = LoggerFactory.getLogger(FormsValidator.class);

        if (data.isPreValidation()) {
            if (logger.isDebugEnabled()) logger.debug("Pre-validating form fields");
            return preValidateForm(formDefinition, data);
        }

        if (logger.isDebugEnabled()) logger.debug("Validating the whole form");
        return validateForm(formDefinition, data);
    }

    private static FormValidationResult preValidateForm(FormDefinition formDefinition, FormData data) {
        final Logger logger = LoggerFactory.getLogger(FormsValidator.class);

        FormValidationResult result = new FormValidationResult();

        result.setFormDefinition(formDefinition);
        result.setPreValidation(true);
        result.setValid(true);

        Map<String, String> validateFields = data.getValidateFields();
        if (validateFields == null || validateFields.isEmpty()) {
            if (logger.isInfoEnabled()) logger.info("No validate fields declared, nothing to validate");
            return result;
        }

        Map<String, FieldDefinition> fieldDefinitions = formDefinition.getFields();

        for (Map.Entry<String, String> entry : validateFields.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            FieldDefinition fieldDefinition;
            try {
                fieldDefinition = FieldDefinitions.findFieldDefinition(
                    fieldDefinitions,
                    null,
                    fieldName,
                    data.getValidateFields(),
                    data.getStateFields()
                );
                if (logger.isDebugEnabled())
                    logger.debug("Found matching field definition of field '{}': {}", fieldName, fieldDefinition);

                if (hasInvalidValues(fieldDefinition, fieldValue)) {
                    if (logger.isInfoEnabled())
                        logger.info("Field value doesn't match the definition for field '{}': {}", fieldName, fieldValue);
                    failure(result, fieldName, fieldValue, new ValidationMessage.Builder()
                        .withValidator("FormsValidator")
                        .withCode(VALIDATION_ERROR_MISMATCHED_FIELD_VALUE)
                        .withArgument(fieldName)
                        .withArgument(fieldValue)
                        .build()
                    );
                    continue;
                }
            } catch (Exception ex) {
                if (logger.isDebugEnabled()) logger.debug("Validation failed!", ex);
                if (logger.isInfoEnabled())
                    logger.info("No field definition found for field '{}', validation failed", fieldName);
                failure(result, fieldName, fieldValue, new ValidationMessage.Builder()
                    .withValidator("FormsValidator")
                    .withCode(VALIDATION_ERROR_MISSING_FIELD_DEFINITION)
                    .withArgument(fieldName)
                    .withArgument(ex.getMessage())
                    .build()
                );
                continue;
            }

            if (logger.isDebugEnabled()) logger.debug("Validating field '{}' with value '{}'", fieldName, fieldValue);

            Map<String, Object> validationRules = fieldDefinition.getValidation();
            if (validationRules == null || validationRules.isEmpty()) {
                if (logger.isInfoEnabled())
                    logger.info("No validation rules specified for field '{}', validation successful", fieldName);
                success(result, fieldName, fieldValue, null);
                continue;
            }

            boolean failed = validateRules(formDefinition, fieldName, fieldValue, validationRules, result);
            if (!failed) {
                if (logger.isInfoEnabled()) logger.info("Field validation successful for field '{}'", fieldName);
                success(result, fieldName, fieldValue, null);
            }
        }

        return result;
    }

    private static FormValidationResult validateForm(FormDefinition formDefinition, FormData data) {
        final Logger logger = LoggerFactory.getLogger(FormsValidator.class);

        FormValidationResult result = new FormValidationResult();

        result.setFormDefinition(formDefinition);
        result.setPreValidation(false);
        result.setValid(true);

        Map<String, String> validateFields = data.getValidateFields();
        if (validateFields == null || validateFields.isEmpty()) {
            if (logger.isInfoEnabled()) logger.info("No validate fields declared, nothing to validate");
            return result;
        }

        Map<String, String> stateFields = data.getStateFields();
        if (stateFields == null) {
            stateFields = Collections.emptyMap();
        }

        Map<String, FieldDefinition> fieldDefinitions = formDefinition.getFields();

        for (Map.Entry<String, String> entry : validateFields.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            FieldDefinition fieldDefinition;
            try {
                fieldDefinition = FieldDefinitions.findFieldDefinition(
                    fieldDefinitions,
                    null,
                    fieldName,
                    data.getValidateFields(),
                    data.getStateFields()
                );
                if (logger.isDebugEnabled())
                    logger.debug("Found matching field definition of field '{}': {}", fieldName, fieldDefinition);

                if (hasInvalidValues(fieldDefinition, fieldValue)) {
                    if (logger.isInfoEnabled())
                        logger.info("Field value doesn't match the definition for field '{}': {}", fieldName, fieldValue);

                    failure(result, fieldName, fieldValue, new ValidationMessage.Builder()
                        .withValidator("FormsValidator")
                        .withCode(VALIDATION_ERROR_MISMATCHED_FIELD_VALUE)
                        .withArgument(fieldName)
                        .withArgument(fieldValue)
                        .build()
                    );
                    continue;
                }
            } catch (Exception ex) {
                if (logger.isInfoEnabled())
                    logger.info("No field definition found for field '{}', validation failed", fieldName, ex);
                failure(result, fieldName, fieldValue, new ValidationMessage.Builder()
                    .withValidator("FormsValidator")
                    .withCode(VALIDATION_ERROR_MISSING_FIELD_DEFINITION)
                    .withArgument(fieldName)
                    .build()
                );
            }

            if (logger.isDebugEnabled())
                logger.debug("Field definition validation for field '{}' and value '{}' done", fieldName, fieldValue);
        }

        for (Map.Entry<String, FieldDefinition> definitionEntry : fieldDefinitions.entrySet()) {
            String fieldName = definitionEntry.getKey();
            FieldDefinition fieldDefinition = definitionEntry.getValue();

            String fieldValue = validateFields.get(fieldName);
            if (fieldValue == null) {
                fieldValue = stateFields.get(fieldName);
            }

            if (logger.isDebugEnabled()) logger.debug("Validating field '{}' with value '{}'", fieldName, fieldValue);

            Map<String, Object> validationRules = fieldDefinition.getValidation();
            if (validationRules == null || validationRules.isEmpty()) {
                if (logger.isInfoEnabled())
                    logger.info("No validation rules specified for field '{}', validation successful", fieldName);
                success(result, fieldName, fieldValue, null);
                continue;
            }

            boolean failed = validateRules(formDefinition, fieldName, fieldValue, validationRules, result);
            if (!failed) {
                if (logger.isInfoEnabled()) logger.info("Field validation successful for field '{}'", fieldName);
                success(result, fieldName, fieldValue, null);
            }
        }

        return result;
    }

    private static boolean hasInvalidValues(FieldDefinition definition, String value) {
        switch (definition.getType()) {
            case TEXT:
            case HIDDEN:
                return false;
            case CHOOSE:
                if (definition.getExternalValueReference() != null) {
                    // ignore external generated values (e.g. customer's IBAN)
                    return false;
                }

                Map<String, Map<String, FieldDefinition>> values = definition.getValues();
                return (!values.keySet().contains(value));
            case CHECK:
                return !VALID_CHECK_VALUES.contains(value);
            default:
                throw new UnsupportedOperationException("Unknown field definition type '" + definition.getType() + "'!");
        }
    }

    private static boolean validateRules(FormDefinition formDefinition, String fieldName, String fieldValue, Map<String, Object> validationRules, FormValidationResult result) {
        final Logger logger = LoggerFactory.getLogger(FormsValidator.class);

        boolean failed = false;
        for (Map.Entry<String, Object> ruleEntry : validationRules.entrySet()) {
            String validatorType = ruleEntry.getKey();

            FormFieldValidator fieldValidator = FIELD_VALIDATORS.get(validatorType);
            if (fieldValidator == null) {
                if (logger.isInfoEnabled())
                    logger.info("No field validator configured for field '{}' and name '{}'", fieldName, validatorType);
                failure(result, fieldName, fieldValue, new ValidationMessage.Builder()
                    .withValidator("FormsValidator")
                    .withCode(VALIDATION_ERROR_MISSING_FIELD_VALIDATOR)
                    .withArgument(validatorType)
                    .withArgument(String.valueOf(FIELD_VALIDATORS.keySet()))
                    .build()
                );
                failed = true;
                continue;
            }

            ValidationMessage message = fieldValidator.validateField(formDefinition, fieldName, fieldValue, ruleEntry.getValue());
            if (message != null) {
                if (logger.isInfoEnabled())
                    logger.info("Field validation failed for field '{}' and validator '{}'", fieldName, validatorType);

                if (fieldValidator.isOptional()) {
                    success(result, fieldName, fieldValue, message);
                } else {
                    failure(result, fieldName, fieldValue, message);
                    failed = true;
                }

                continue;
            }

            if (logger.isDebugEnabled())
                logger.debug("Field validation successful for field '{}' and validator '{}'", fieldName, validatorType);
        }

        return failed;
    }

    private static void success(FormValidationResult formResult, String fieldName, Object value, ValidationMessage hint) {
        FormFieldValidationResult result = formResult.findOrCreateFieldValidationResult(fieldName);
        result.setValue(value);

        if (hint != null) {
            result.addHint(hint);
        }

        formResult.addFieldValidationResult(fieldName, result);
    }

    private static void failure(FormValidationResult formResult, String fieldName, Object value, ValidationMessage message) {
        formResult.setValid(false);

        FormFieldValidationResult result = formResult.findOrCreateFieldValidationResult(fieldName);
        result.setValue(value);

        if (message != null) {
            result.addErrorMessage(message);
        }

        formResult.addFieldValidationResult(fieldName, result);
    }
}

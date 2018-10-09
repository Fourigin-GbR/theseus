package com.fourigin.argo.forms.validation;

import com.fourigin.argo.forms.definition.FieldDefinition;
import com.fourigin.argo.forms.definition.FormDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class FormsValidator {

    public static final String VALIDATION_ERROR_MISSING_FIELD_DEFINITION = "MISSING_FIELD_DEFINITION";
    public static final String VALIDATION_ERROR_MISSING_FIELD_VALIDATOR = "MISSING_FIELD_VALIDATOR";

    private static final Map<String, FormFieldValidator> FIELD_VALIDATORS;

    static {
        FIELD_VALIDATORS = new HashMap<>();
        FIELD_VALIDATORS.put("mandatory", new MandatoryFormFieldValidator());
        FIELD_VALIDATORS.put("pattern", new PatternMatchingFormFieldValidator());
    }

    private FormsValidator() {
    }

    public static FormValidationResult validate(FormDefinition formDefinition, FormData data) {
        Objects.requireNonNull(data, "FormData must not be null!");
        Objects.requireNonNull(formDefinition, "FormDefinition must not be null!");

        final Logger logger = LoggerFactory.getLogger(FormsValidator.class);

        if(data.isPreValidation()){
            if (logger.isDebugEnabled()) logger.debug("Pre-validating form fields");
            return preValidateForm(formDefinition, data);
        }

        if (logger.isDebugEnabled()) logger.debug("Validating the whole form");
        return validateForm(formDefinition, data);
    }

    private static FormValidationResult preValidateForm(FormDefinition formDefinition, FormData data){
        final Logger logger = LoggerFactory.getLogger(FormsValidator.class);

        FormValidationResult result = new FormValidationResult();

        result.setFormDefinitionId(data.getFormDefinitionId());
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

            FieldDefinition fieldDefinition = fieldDefinitions.get(fieldName);
            if (fieldDefinition == null) {
                if (logger.isInfoEnabled())
                    logger.info("No field definition found for field '{}', validation failed", fieldName);
                failure(result, fieldName, fieldValue, new FailureReason.Builder()
                    .withValidator("FormsValidator")
                    .withCode(VALIDATION_ERROR_MISSING_FIELD_DEFINITION)
                    .build()
                );
                continue;
            }

            if (logger.isDebugEnabled()) logger.debug("Validating field '{}' with value '{}'", fieldName, fieldValue);

            Map<String, Object> validationRules = fieldDefinition.getValidation();
            if (validationRules == null || validationRules.isEmpty()) {
                if (logger.isInfoEnabled())
                    logger.info("No validation rules specified for field '{}', validation successful", fieldName);
                success(result, fieldName, fieldValue);
                continue;
            }

            boolean failed = validateRules(formDefinition, fieldName, fieldValue, validationRules, result);
            if(!failed) {
                if (logger.isInfoEnabled()) logger.info("Field validation successful for field '{}'", fieldName);
                success(result, fieldName, fieldValue);
            }
        }

        return result;
    }

    private static FormValidationResult validateForm(FormDefinition formDefinition, FormData data){
        final Logger logger = LoggerFactory.getLogger(FormsValidator.class);

        FormValidationResult result = new FormValidationResult();

        result.setFormDefinitionId(data.getFormDefinitionId());
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

            FieldDefinition fieldDefinition = fieldDefinitions.get(fieldName);
            if (fieldDefinition == null) {
                if (logger.isInfoEnabled())
                    logger.info("No field definition found for field '{}', validation failed", fieldName);
                failure(result, fieldName, fieldValue, new FailureReason.Builder()
                    .withValidator("FormsValidator")
                    .withCode(VALIDATION_ERROR_MISSING_FIELD_DEFINITION)
                    .build()
                );
            }
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
                success(result, fieldName, fieldValue);
                continue;
            }

            boolean failed = validateRules(formDefinition, fieldName, fieldValue, validationRules, result);
            if(!failed) {
                if (logger.isInfoEnabled()) logger.info("Field validation successful for field '{}'", fieldName);
                success(result, fieldName, fieldValue);
            }
        }

        return result;
    }

    private static boolean validateRules(FormDefinition formDefinition, String fieldName, String fieldValue, Map<String, Object> validationRules, FormValidationResult result){
        final Logger logger = LoggerFactory.getLogger(FormsValidator.class);

        boolean failed = false;
        for (Map.Entry<String, Object> ruleEntry : validationRules.entrySet()) {
            String validatorType = ruleEntry.getKey();

            FormFieldValidator fieldValidator = FIELD_VALIDATORS.get(validatorType);
            if (fieldValidator == null) {
                if (logger.isInfoEnabled())
                    logger.info("No field validator configured for field '{}' and name '{}'", fieldName, validatorType);
                failure(result, fieldName, fieldValue, new FailureReason.Builder()
                    .withValidator("FormsValidator")
                    .withCode(VALIDATION_ERROR_MISSING_FIELD_VALIDATOR)
                    .withArgument("type", validatorType)
                    .build()
                );
                failed = true;
                continue;
            }

            FailureReason failureReason = fieldValidator.validateField(formDefinition, fieldName, fieldValue, ruleEntry.getValue());
            if (failureReason != null) {
                if (logger.isInfoEnabled())
                    logger.info("Field validation failed for field '{}' and validator '{}'", fieldName, validatorType);
                failure(result, fieldName, fieldValue, failureReason);
                failed = true;
                continue;
            }

            if (logger.isDebugEnabled())
                logger.debug("Field validation successful for field '{}' and validator '{}'", fieldName, validatorType);
        }

        return failed;
    }

    private static void success(FormValidationResult formResult, String fieldName, Object value) {
        Map<String, FormFieldValidationResult> fields = formResult.getFields();
        if (fields == null) {
            fields = new HashMap<>();
            formResult.setFields(fields);
        }

        FormFieldValidationResult result = fields.get(fieldName);
        if (result == null) {
            result = new FormFieldValidationResult();
            result.setValue(value);
            fields.put(fieldName, result);
        }

        result.setValid(true);
    }

    private static void failure(FormValidationResult formResult, String fieldName, Object value, FailureReason failureReason) {
        formResult.setValid(false);

        Map<String, FormFieldValidationResult> fields = formResult.getFields();
        if (fields == null) {
            fields = new HashMap<>();
            formResult.setFields(fields);
        }

        FormFieldValidationResult result = fields.get(fieldName);
        if (result == null) {
            result = new FormFieldValidationResult();
            result.setValue(value);
            fields.put(fieldName, result);
        }

        result.setValid(false);

        List<FailureReason> reasons = result.getFailureReasons();
        if (reasons == null) {
            reasons = new ArrayList<>();
            result.setFailureReasons(reasons);
        }

        reasons.add(failureReason);
    }
}

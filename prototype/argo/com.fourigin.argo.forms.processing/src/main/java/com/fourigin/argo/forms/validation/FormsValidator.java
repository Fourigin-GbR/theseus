package com.fourigin.argo.forms.validation;

import com.fourigin.argo.forms.definition.FieldDefinition;
import com.fourigin.argo.forms.definition.FormDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
    }

    private FormsValidator() {
    }

    public static FormValidationResult validate(FormDefinition formDefinition, FormData data) {
        Objects.requireNonNull(data, "FormData must not be null!");
        Objects.requireNonNull(formDefinition, "FormDefinition must not be null!");

        final Logger logger = LoggerFactory.getLogger(FormsValidator.class);

        FormValidationResult result = new FormValidationResult();

        result.setFormDefinitionId(data.getFormDefinitionId());
        result.setPreValidation(data.isPreValidation());

        Map<String, String> validateFields = data.getValidateFields();
        if (validateFields == null || validateFields.isEmpty()) {
            if (logger.isInfoEnabled()) logger.info("No validate fields declared, nothing to validate");
            result.setValid(true);
            return result;
        }

        Map<String, FieldDefinition> fieldDefinitions = formDefinition.getFields();

        for (Map.Entry<String, String> entry : validateFields.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if (logger.isDebugEnabled()) logger.debug("Validating field '{}' with value '{}'", fieldName, fieldValue);

            FieldDefinition fieldDefinition = fieldDefinitions.get(fieldName);
            if (fieldDefinition == null) {
                if (logger.isInfoEnabled()) logger.info("No field definition found for field '{}', validation failed", fieldName);

                result.setValid(false);
                failure(result, fieldName, fieldValue, new FailureReason.Builder()
                        .withValidator("FormsValidator")
                        .withCode(VALIDATION_ERROR_MISSING_FIELD_DEFINITION)
                        .build()
                );
                continue;
            }

            Map<String, Object> validationRules = fieldDefinition.getValidation();
            if (validationRules == null || validationRules.isEmpty()) {
                if (logger.isInfoEnabled()) logger.info("No validation rules specified for field '{}', validation successful", fieldName);
                success(result, fieldName, fieldValue);
                continue;
            }

            for (Map.Entry<String, Object> ruleEntry : validationRules.entrySet()) {
                String validatorType = ruleEntry.getKey();

                FormFieldValidator fieldValidator = FIELD_VALIDATORS.get(validatorType);
                if (fieldValidator == null) {
                    if (logger.isInfoEnabled()) logger.info("No field validator configured for field '{}' and name '{}'", fieldName, validatorType);
                    failure(result, fieldName, fieldValue, new FailureReason.Builder()
                        .withValidator("FormsValidator")
                        .withCode(VALIDATION_ERROR_MISSING_FIELD_VALIDATOR)
                        .withArgument("type", validatorType)
                        .build()
                    );
                    continue;
                }

                FailureReason failureReason = fieldValidator.validateField(fieldName, fieldValue, ruleEntry.getValue());
                if(failureReason != null){
                    if (logger.isInfoEnabled()) logger.info("Field validation failed for field '{}' and validator '{}'", fieldName, validatorType);
                    failure(result, fieldName, fieldValue, failureReason);
                    continue;
                }

                if (logger.isDebugEnabled()) logger.debug("Field validation successful for field '{}' and validator '{}'", fieldName, validatorType);
            }

            if (logger.isInfoEnabled()) logger.info("Field validation successful for field '{}'", fieldName);
        }

        return result;
    }

    private static void success(FormValidationResult formResult, String fieldName, Object value) {
        Map<String, FormFieldValidationResult> fields = formResult.getFields();
        if (fields == null) {
            fields = new HashMap<>();
        }

        FormFieldValidationResult result = fields.get(fieldName);
        if (result == null) {
            result = new FormFieldValidationResult();
            result.setValue(value);
        }

        result.setValid(true);
    }

    private static void failure(FormValidationResult formResult, String fieldName, Object value, FailureReason failureReason) {
        Map<String, FormFieldValidationResult> fields = formResult.getFields();
        if (fields == null) {
            fields = new HashMap<>();
        }

        FormFieldValidationResult result = fields.get(fieldName);
        if (result == null) {
            result = new FormFieldValidationResult();
            result.setValue(value);
        }

        result.setValid(false);

        List<FailureReason> reasons = result.getFailureReasons();
        if (reasons == null) {
            reasons = new ArrayList<>();
        }

        reasons.add(failureReason);
    }
}

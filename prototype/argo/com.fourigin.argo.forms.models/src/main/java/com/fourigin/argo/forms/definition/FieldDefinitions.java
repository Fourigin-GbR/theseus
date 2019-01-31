package com.fourigin.argo.forms.definition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public final class FieldDefinitions {
    private FieldDefinitions() {
    }

    public static FieldDefinition findFieldDefinition(
        Map<String, FieldDefinition> fieldDefinitions,
        String baseName,
        String fieldName,
        Map<String, String> validateFields,
        Map<String, String> stateFields
    ) {
        Logger logger = LoggerFactory.getLogger(FieldDefinitions.class);

        if (logger.isDebugEnabled()) logger.debug("Searching for field definition of '{}' inside of {}", fieldName, fieldDefinitions);
        if (logger.isDebugEnabled()) logger.debug("validateFields: {}", validateFields);

        FieldDefinition result = resolveSingleFieldReference(fieldDefinitions, fieldName);
        if (result != null)
            return result;

        // field chain reference, search inside of value contexts

        int pos = fieldName.indexOf('/');
        String parentFieldName = fieldName.substring(0, pos);
        String childFieldName = fieldName.substring(pos + 1);

        if (logger.isDebugEnabled()) logger.debug("parent name: '{}', child name: '{}'", parentFieldName, childFieldName);

        FieldDefinition parentField = fieldDefinitions.get(parentFieldName);
        if (parentField == null) {
            throw new IllegalArgumentException("No field definition found for the parent reference '" + parentFieldName + "'!");
        }

        Map<String, Map<String, FieldDefinition>> values = parentField.getValues();
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("No field values found!");
        }

        String subBase = baseName == null ? parentFieldName : baseName + '/' + parentFieldName;
        String parentFieldValue = getFieldValue(validateFields, stateFields, subBase);
        if (parentFieldValue == null) {
            throw new IllegalArgumentException("No field value found for the parent reference '" + parentFieldName + "'!");
        }

        Map<String, FieldDefinition> valueContext = values.get(parentFieldValue);
        if (valueContext == null || valueContext.isEmpty()) {
            throw new IllegalArgumentException("No value context found for field value '" + parentFieldValue + "'!");
        }

        return findFieldDefinition(valueContext, subBase, childFieldName, validateFields, stateFields);
    }

    public static FieldDefinition findFieldDefinition(
        Map<String, FieldDefinition> fieldDefinitions,
        String fieldReference
    ) {
        FieldDefinition result = resolveSingleFieldReference(fieldDefinitions, fieldReference);
        if (result != null) return result;

        // field chain reference, search inside of value contexts

        int pos = fieldReference.indexOf('/');
        String parentFieldRef = fieldReference.substring(0, pos);
        String childFieldName = fieldReference.substring(pos + 1);

        int valuePos = parentFieldRef.indexOf(":");
        if (valuePos < 0) {
            throw new IllegalArgumentException("No field value found for the parent reference '" + parentFieldRef + "'!");
        }
        String parentFieldName = parentFieldRef.substring(0, valuePos);
        String parentFieldValue = parentFieldRef.substring(valuePos + 1);

        FieldDefinition parentField = fieldDefinitions.get(parentFieldName);
        if (parentField == null) {
            throw new IllegalArgumentException("No field definition found for the parent reference '" + parentFieldName + "'!");
        }

        Map<String, Map<String, FieldDefinition>> values = parentField.getValues();
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("No field values found!");
        }

        Map<String, FieldDefinition> valueContext = values.get(parentFieldValue);
        if (valueContext == null || valueContext.isEmpty()) {
            throw new IllegalArgumentException("No value context found for field value '" + parentFieldValue + "'!");
        }

        return findFieldDefinition(valueContext, childFieldName);
    }

    private static FieldDefinition resolveSingleFieldReference(Map<String, FieldDefinition> fieldDefinitions, String fieldName) {
        if (fieldName.contains("/")) {
            return null;
        }

        // single field reference
        FieldDefinition result = fieldDefinitions.get(fieldName);
        if (result == null) {
            throw new IllegalArgumentException("No field definition found for the reference '" + fieldName + "'!");
        }
        
        return result;
    }

    private static String getFieldValue(Map<String, String> validateFields, Map<String, String> stateFields, String fieldName) {
        String value = null;

        if (validateFields != null && !validateFields.isEmpty()) {
            value = validateFields.get(fieldName);
        }

        if (value != null) {
            return value;
        }

        if (stateFields != null && !stateFields.isEmpty()) {
            value = stateFields.get(fieldName);
        }

        return value;
    }
}

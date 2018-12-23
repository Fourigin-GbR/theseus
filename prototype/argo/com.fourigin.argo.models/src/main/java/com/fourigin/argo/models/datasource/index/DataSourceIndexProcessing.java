package com.fourigin.argo.models.datasource.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DataSourceIndexProcessing {
    private DataSourceIndexProcessing(){
    }

    public static List<String> resolveMatchingIndexTargets(
        DataSourceIndex index,
        Map<String, Set<String>> filterCategories,
        Map<String, FieldValueComparator> fieldComparators
    ){
        Logger logger = LoggerFactory.getLogger(DataSourceIndexProcessing.class);

        List<String> references = index.getReferences();
        if (logger.isDebugEnabled()) logger.debug("References: {}", references);

        Map<String, Boolean> matchingFlags = new HashMap<>();
        for (String reference : references) {
            // initialization with 'null'
            matchingFlags.put(reference, null);
        }

        // categories
        if (filterCategories != null && !filterCategories.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("Filtering by categories ...");
            for (Map.Entry<String, Set<String>> entry : filterCategories.entrySet()) {
                String categoryName = entry.getKey();
                if (logger.isDebugEnabled()) logger.debug("Verifying category '{}'", categoryName);

                Map<String, List<Integer>> matchingCategory = index.getCategories().get(categoryName);
                if (logger.isDebugEnabled()) logger.debug("Corresponding index entry: {}", matchingCategory);

                if (matchingCategory == null) {
                    if (logger.isDebugEnabled())
                        logger.debug("No indexed category found for name '{}'", categoryName);
                    // TODO: return an empty list?
                    continue;
                }

                Set<String> categoryValues = entry.getValue();
                if (logger.isDebugEnabled()) logger.debug("Requested category values: {}", categoryValues);

                for (String categoryValue : categoryValues) {
                    if (logger.isDebugEnabled()) logger.debug("Verifying category value '{}'", categoryValue);
                    List<Integer> matchingReferenceNumbers = matchingCategory.get(categoryValue);
                    if (matchingReferenceNumbers != null && !matchingReferenceNumbers.isEmpty()) {
                        if (logger.isDebugEnabled()) logger.debug("Found matches: {}", matchingReferenceNumbers);
                        for (Integer referenceNumber : matchingReferenceNumbers) {
                            flagMatchingReference(referenceNumber, references, matchingFlags, "category: " + categoryName + ":" + categoryValue);
//                            String reference = references.get(referenceNumber);
//                            Boolean previousFlag = matchingFlags.get(reference);
//                            if(previousFlag == null || !previousFlag) {
//                                if (logger.isDebugEnabled()) logger.debug("Reference '{}' is matching the category '{}:{}'", reference, categoryName, categoryValue);
//                                matchingFlags.put(reference, true);
//                            }
                        }
                    } else {
                        if (logger.isDebugEnabled())
                            logger.debug("No matches found for category value '{}'", categoryValue);
                    }
                }
            }
        }

        // fields
        if (fieldComparators != null && !fieldComparators.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("Filtering by field comparators ...");

            List<FieldValue> fields = index.getFields();
            Map<String, FieldValue> mappedFields = new HashMap<>();
            for (FieldValue field : fields) {
                mappedFields.put(field.getName(), field);
            }

            for (Map.Entry<String, FieldValueComparator> entry : fieldComparators.entrySet()) {
                String fieldName = entry.getKey();
                FieldValueComparator fieldComparator = entry.getValue();
                if (logger.isDebugEnabled())
                    logger.debug("Verifying comparator for field '{}': {}", fieldName, fieldComparator);

                FieldValue fieldValue = mappedFields.get(fieldName);
                if (logger.isDebugEnabled()) logger.debug("Comparing with {}", fieldValue);

                int referenceNumber = 0;
                for (String value : fieldValue.getValue()) {
                    if (isMatching(fieldComparator, fieldValue.getType(), value)) {
                        flagMatchingReference(referenceNumber, references, matchingFlags, "field: " + fieldName + ":" + value);
                    } else {
                        flagNotMatchingReference(referenceNumber, references, matchingFlags, "field: " + fieldName + ":" + value);
                    }

                    referenceNumber++;
                }
            }
        }

        // filtering the result
        if (logger.isDebugEnabled()) logger.debug("Filtering references with matching flags: {}", matchingFlags);
        Iterator<String> iter = references.iterator();
        while (iter.hasNext()) {
            String reference = iter.next();
            Boolean flag = matchingFlags.get(reference);
            if (flag == null || !flag) {
                iter.remove();
            }
        }

        return references;
    }

    private static boolean isMatching(FieldValueComparator fieldComparator, FieldType type, String value) {
        Logger logger = LoggerFactory.getLogger(DataSourceIndexProcessing.class);

        String comparatorName = fieldComparator.getComparator();
        FieldValueComparatorType comparatorType = FieldValueComparatorType.valueOf(comparatorName);

        String comparatorValue = fieldComparator.getValue();

        switch (comparatorType) {
            case EQUAL:
                if (logger.isDebugEnabled()) logger.debug("IS EQUAL: {}, {} with type {}", value, comparatorValue, type);
                return isValueEqual(type, value, comparatorValue);
            case LESS_THEN:
                if (logger.isDebugEnabled()) logger.debug("IS LESS THEN: {}, {} with type {}", value, comparatorValue, type);
                return isValueLessThen(type, value, comparatorValue);
            case LESS_THEN_OR_EQUAL:
                if (logger.isDebugEnabled()) logger.debug("IS LESS THEN OR EQUAL: {}, {} with type {}", value, comparatorValue, type);
                return isValueLessThenOrEqual(type, value, comparatorValue);
            case GREATER_THEN:
                if (logger.isDebugEnabled()) logger.debug("IS GREATER THEN: {}, {} with type {}", value, comparatorValue, type);
                return isValueGreaterThen(type, value, comparatorValue);
            case GREATER_THEN_OR_EQUAL:
                if (logger.isDebugEnabled()) logger.debug("IS GREATER THEN OR EQUAL: {}, {} with type {}", value, comparatorValue, type);
                return isValueGreaterThenOrEqual(type, value, comparatorValue);
            case BETWEEN:
                if (logger.isDebugEnabled()) logger.debug("IS IN BETWEEN: {}, {} with type {}", value, comparatorValue, type);
                return isValueInBetween(type, value, comparatorValue);
            case NOT_EQUAL:
                if (logger.isDebugEnabled()) logger.debug("IS NOT EQUAL: {}, {} with type {}", value, comparatorValue, type);
                return isValueNotEqual(type, value, comparatorValue);
            default:
                throw new UnsupportedOperationException("Unable to perform the matching check for unknown comparator type '" + comparatorType + "'!");
        }
    }

    private static boolean isValueEqual(FieldType type, String fieldValue, String comparatorValue) {
        switch (type) {
            case TEXT:
                return fieldValue.equals(comparatorValue);
            case NUMBER:
            case PRICE:
                double parsedFieldValue = Double.parseDouble(fieldValue);
                double parsedComparatorValue = Double.parseDouble(comparatorValue);
                return parsedFieldValue == parsedComparatorValue;
//            case DATE:
//                break;
            default:
                throw new UnsupportedOperationException("Can't verify is the value is equal to " + comparatorValue + " for type '" + type + "'!");
        }
    }

    private static boolean isValueNotEqual(FieldType type, String fieldValue, String comparatorValue) {
        switch (type) {
            case TEXT:
                return !fieldValue.equals(comparatorValue);
            case NUMBER:
            case PRICE:
                double parsedFieldValue = Double.parseDouble(fieldValue);
                double parsedComparatorValue = Double.parseDouble(comparatorValue);
                return parsedFieldValue != parsedComparatorValue;
//            case DATE:
//                break;
            default:
                throw new UnsupportedOperationException("Can't verify is the value is not equal to " + comparatorValue + " for type '" + type + "'!");
        }
    }

    private static boolean isValueLessThen(FieldType type, String fieldValue, String comparatorValue) {
        switch (type) {
            case NUMBER:
            case PRICE:
                double parsedFieldValue = Double.parseDouble(fieldValue);
                double parsedComparatorValue = Double.parseDouble(comparatorValue);
                return parsedFieldValue < parsedComparatorValue;
//            case DATE:
//                break;
            default:
                throw new UnsupportedOperationException("Can't verify is the value is less then " + comparatorValue + " for type '" + type + "'!");
        }
    }

    private static boolean isValueLessThenOrEqual(FieldType type, String fieldValue, String comparatorValue) {
        switch (type) {
            case NUMBER:
            case PRICE:
                double parsedFieldValue = Double.parseDouble(fieldValue);
                double parsedComparatorValue = Double.parseDouble(comparatorValue);
                return parsedFieldValue <= parsedComparatorValue;
//            case DATE:
//                break;
            default:
                throw new UnsupportedOperationException("Can't verify is the value is less then or equal " + comparatorValue + " for type '" + type + "'!");
        }
    }

    private static boolean isValueGreaterThen(FieldType type, String fieldValue, String comparatorValue) {
        switch (type) {
            case NUMBER:
            case PRICE:
                double parsedFieldValue = Double.parseDouble(fieldValue);
                double parsedComparatorValue = Double.parseDouble(comparatorValue);
                return parsedFieldValue > parsedComparatorValue;
//            case DATE:
//                break;
            default:
                throw new UnsupportedOperationException("Can't verify is the value is greather then " + comparatorValue + " for type '" + type + "'!");
        }
    }

    private static boolean isValueGreaterThenOrEqual(FieldType type, String fieldValue, String comparatorValue) {
        switch (type) {
            case NUMBER:
            case PRICE:
                double parsedFieldValue = Double.parseDouble(fieldValue);
                double parsedComparatorValue = Double.parseDouble(comparatorValue);
                return parsedFieldValue >= parsedComparatorValue;
//            case DATE:
//                break;
            default:
                throw new UnsupportedOperationException("Can't verify is the value is greather then or equal " + comparatorValue + " for type '" + type + "'!");
        }
    }

    private static boolean isValueInBetween(FieldType type, String fieldValue, String comparatorValue) {
        String[] parts = comparatorValue.split(",");
        if(parts.length != 2){
            throw new IllegalArgumentException("Comparator value '" + comparatorValue + "' can't be used as an inbetween value! Required is a value like <from>:<to>, e.g. \"1, 5\"");
        }

        String fromValue = parts[0].trim();
        String toValue = parts[1].trim();

        switch (type) {
            case NUMBER:
            case PRICE:
                double parsedFieldValue = Double.parseDouble(fieldValue);
                double parsedComparatorFromValue = Double.parseDouble(fromValue);
                double parsedComparatorToValue = Double.parseDouble(toValue);
                return parsedFieldValue >= parsedComparatorFromValue && parsedFieldValue <= parsedComparatorToValue;
//            case DATE:
//                break;
            default:
                throw new UnsupportedOperationException("Can't verify is the value is in between of " + comparatorValue + " for type '" + type + "'!");
        }
    }

    private static void flagMatchingReference(int referenceNumber, List<String> references, Map<String, Boolean> matchingFlags, String criteria) {
        Logger logger = LoggerFactory.getLogger(DataSourceIndexProcessing.class);

        if (logger.isDebugEnabled()) logger.debug("Trying to flag reference #{} with criteria {}", referenceNumber, criteria);

        String reference = references.get(referenceNumber);
        Boolean previousFlag = matchingFlags.get(reference);
        if (logger.isDebugEnabled()) logger.debug("Previous flag value: {}", previousFlag);
        if (previousFlag == null) {
            if (logger.isDebugEnabled())
                logger.debug("Flag the reference '{}' as matching the search criteria '{}'", reference, criteria);
            matchingFlags.put(reference, true);
        }
        else if(previousFlag && logger.isDebugEnabled()){
            logger.debug("The reference '{}' is matching the search criteria '{}' but is already flagged.", reference, criteria);
        }
    }

    private static void flagNotMatchingReference(int referenceNumber, List<String> references, Map<String, Boolean> matchingFlags, String criteria) {
        Logger logger = LoggerFactory.getLogger(DataSourceIndexProcessing.class);

        if (logger.isDebugEnabled()) logger.debug("Flag not matching reference #{} with criteria {}", referenceNumber, criteria);

        String reference = references.get(referenceNumber);
        matchingFlags.put(reference, false);
    }
}

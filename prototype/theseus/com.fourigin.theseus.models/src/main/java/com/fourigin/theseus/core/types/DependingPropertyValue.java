package com.fourigin.theseus.core.types;

import com.fourigin.theseus.core.PropertyAvailability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DependingPropertyValue<T> implements PropertyValue{
    private PropertyAvailability availability;
    private SetPropertyType responsibleType;
    private Map<String, T> values;

    public DependingPropertyValue(PropertyAvailability availability, SetPropertyType responsibleType, Map<String, T> values) {
        Objects.requireNonNull(values, "values must not be null!");

        this.availability = availability;
        this.responsibleType = responsibleType;
        this.values = new HashMap<>();

        Set<String> predefinedKeys = responsibleType.getKeys();

        Set<String> unexpectedKeys = new HashSet<>();
        for (Map.Entry<String, T> entry : values.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue();
            if(!predefinedKeys.contains(key)){
                unexpectedKeys.add(key);
                continue;
            }

            values.put(key, value);
        }

        if(!unexpectedKeys.isEmpty()){
            throw new IllegalArgumentException("Found unexpected keys: " + unexpectedKeys + "!\nExpected are only following keys: " + predefinedKeys);
        }
    }

    public PropertyAvailability getAvailability() {
        return availability;
    }

    public Map<String, T> getValues() {
        return values;
    }

    public SetPropertyType getResponsibleType() {
        return responsibleType;
    }
}

package com.fourigin.theseus.core.types;

import com.fourigin.theseus.core.PropertyAvailability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SetPropertyValue<T> implements PropertyValue<SetPropertyType>{
    private SetPropertyType type;
    private PropertyAvailability availability;
    private Map<String, T> values;

    public SetPropertyValue(SetPropertyType type, PropertyAvailability availability, Map<String, T> values) {
        Objects.requireNonNull(values, "values must not be null!");

        this.type = type;
        this.availability = availability;
        this.values = new HashMap<>();

        Set<String> predefinedKeys = type.getKeys();

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

    @Override
    public SetPropertyType getType() {
        return type;
    }

    @Override
    public PropertyAvailability getAvailability() {
        return availability;
    }

    public Map<String, T> getValues() {
        return values;
    }

    public void setValues(Map<String, T> values) {
        this.values = values;
    }
}

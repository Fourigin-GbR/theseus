package com.fourigin.theseus.core.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SetPropertyValue extends HashMap<String, SetPropertyValueEntry> implements PropertyValue {

    private static final long serialVersionUID = 3111692058610082544L;

    public SetPropertyValue(SetPropertyType type, Map<String, SetPropertyValueEntry> entries) {
        Objects.requireNonNull(type, "type must not be null!");

        Set<String> predefinedKeys = type.getKeys();
        Objects.requireNonNull(predefinedKeys, "predefinedLKeys of the type must not be null!");

        Set<String> unexpectedKeys = new HashSet<>();
        for (Map.Entry<String, SetPropertyValueEntry> entry : entries.entrySet()) {
            String propertyCode = entry.getKey();
            SetPropertyValueEntry valueEntry = entry.getValue();

            if(!predefinedKeys.contains(propertyCode)){
                unexpectedKeys.add(propertyCode);
                continue;
            }

            this.put(propertyCode, valueEntry);
        }

        if(!unexpectedKeys.isEmpty()){
            throw new IllegalArgumentException("Found unexpected keys " + unexpectedKeys + "! Expected are only " + predefinedKeys);
        }
    }
}

package com.fourigin.theseus.core.types;

import com.fourigin.theseus.core.PropertyAvailability;

public interface PropertyValue<T extends PropertyType> {
    T getType();
    PropertyAvailability getAvailability();
}

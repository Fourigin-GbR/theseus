package com.fourigin.theseus.core;

import com.fourigin.theseus.core.types.PropertyType;
import com.fourigin.theseus.core.types.PropertyValue;

public class Property<T extends PropertyType> {
    private PropertyDefinition<T> definition;
    private PropertyValue<T> propertyValue;

    public PropertyDefinition<T> getDefinition() {
        return definition;
    }

    public void setDefinition(PropertyDefinition<T> definition) {
        this.definition = definition;
    }

    public PropertyValue<T> getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(PropertyValue<T> propertyValue) {
        this.propertyValue = propertyValue;
    }
}

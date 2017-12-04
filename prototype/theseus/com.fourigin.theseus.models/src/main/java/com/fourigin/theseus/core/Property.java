package com.fourigin.theseus.core;

import com.fourigin.theseus.core.types.PropertyType;
import com.fourigin.theseus.core.types.PropertyValue;

public class Property<T extends PropertyType> {
    private String code;
    private PropertyDefinition<T> definition;
    private PropertyValue propertyValue;

    public Property() {
    }

    public Property(String code, PropertyDefinition<T> definition, PropertyValue propertyValue) {
        this.code = code;
        this.definition = definition;
        this.propertyValue = propertyValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PropertyDefinition<T> getDefinition() {
        return definition;
    }

    public void setDefinition(PropertyDefinition<T> definition) {
        this.definition = definition;
    }

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(PropertyValue propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property)) return false;

        Property<?> property = (Property<?>) o;

        if (code != null ? !code.equals(property.code) : property.code != null) return false;
        //noinspection SimplifiableIfStatement
        if (definition != null ? !definition.equals(property.definition) : property.definition != null) return false;
        return propertyValue != null ? propertyValue.equals(property.propertyValue) : property.propertyValue == null;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (definition != null ? definition.hashCode() : 0);
        result = 31 * result + (propertyValue != null ? propertyValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Property{" +
            "code='" + code + '\'' +
            ", definition=" + definition +
            ", propertyValue=" + propertyValue +
            '}';
    }
}

package com.fourigin.theseus.core;

import com.fourigin.theseus.core.types.PropertyType;

public class PropertyDefinition<T extends PropertyType> {
    private String code;
    private T propertyType;
    private Translation name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(T propertyType) {
        this.propertyType = propertyType;
    }

    public Translation getName() {
        return name;
    }

    public void setName(Translation name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyDefinition)) return false;

        PropertyDefinition property = (PropertyDefinition) o;

        if (code != null ? !code.equals(property.code) : property.code != null) return false;
        //noinspection SimplifiableIfStatement
        if (propertyType != null ? !propertyType.equals(property.propertyType) : property.propertyType != null)
            return false;
        return name != null ? name.equals(property.name) : property.name == null;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (propertyType != null ? propertyType.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Property{" +
            "code='" + code + '\'' +
            ", propertyType=" + propertyType +
            ", name=" + name +
            '}';
    }
}

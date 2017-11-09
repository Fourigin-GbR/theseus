package com.fourigin.theseus.core.updates;

public class AddProperty implements ProductChange {
    private String propertyCode;

    public String getPropertyCode() {
        return propertyCode;
    }

    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddProperty)) return false;

        AddProperty that = (AddProperty) o;

        return propertyCode != null ? propertyCode.equals(that.propertyCode) : that.propertyCode == null;
    }

    @Override
    public int hashCode() {
        return propertyCode != null ? propertyCode.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AddProperty{" +
            "propertyCode='" + propertyCode + '\'' +
            '}';
    }
}

package com.fourigin.theseus.core.updates;

public class DeleteProperty implements ProductChange {
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
        if (!(o instanceof DeleteProperty)) return false;

        DeleteProperty that = (DeleteProperty) o;

        return propertyCode != null ? propertyCode.equals(that.propertyCode) : that.propertyCode == null;
    }

    @Override
    public int hashCode() {
        return propertyCode != null ? propertyCode.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DeleteProperty{" +
            "propertyCode='" + propertyCode + '\'' +
            '}';
    }
}

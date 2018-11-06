package com.fourigin.argo.forms.definition;

import java.util.Objects;

public class ExternalValueReference {
    private String owner;
    private String value;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExternalValueReference)) return false;
        ExternalValueReference that = (ExternalValueReference) o;
        return Objects.equals(owner, that.owner) &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, value);
    }

    @Override
    public String toString() {
        return "ExternalValueReference{" +
            "owner='" + owner + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}

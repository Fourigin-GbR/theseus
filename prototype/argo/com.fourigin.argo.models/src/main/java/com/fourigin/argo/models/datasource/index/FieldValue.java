package com.fourigin.argo.models.datasource.index;

import java.io.Serializable;
import java.util.Objects;

public class FieldValue implements Serializable {

    private static final long serialVersionUID = 5506494158223786807L;

    private String name;
    private FieldType type;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
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
        if (!(o instanceof FieldValue)) return false;
        FieldValue that = (FieldValue) o;
        return Objects.equals(name, that.name) &&
            type == that.type &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, value);
    }

    @Override
    public String toString() {
        return "FieldValue{" +
            "name='" + name + '\'' +
            ", type=" + type +
            ", value='" + value + '\'' +
            '}';
    }
}

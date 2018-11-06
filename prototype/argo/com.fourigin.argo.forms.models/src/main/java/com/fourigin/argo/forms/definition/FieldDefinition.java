package com.fourigin.argo.forms.definition;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class FieldDefinition implements Serializable {
    private static final long serialVersionUID = -1526220166480888543L;

    private Type type;
    private Map<String, Object> validation;
    private Map<String, Map<String, FieldDefinition>> values;
    private ExternalValueReference externalValueReference;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Map<String, Object> getValidation() {
        return validation;
    }

    public void setValidation(Map<String, Object> validation) {
        this.validation = validation;
    }

    public Map<String, Map<String, FieldDefinition>> getValues() {
        return values;
    }

    public void setValues(Map<String, Map<String, FieldDefinition>> values) {
        this.values = values;
    }

    public ExternalValueReference getExternalValueReference() {
        return externalValueReference;
    }

    public void setExternalValueReference(ExternalValueReference externalValueReference) {
        this.externalValueReference = externalValueReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldDefinition)) return false;
        FieldDefinition that = (FieldDefinition) o;
        return type == that.type &&
            Objects.equals(validation, that.validation) &&
            Objects.equals(values, that.values) &&
            Objects.equals(externalValueReference, that.externalValueReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, validation, values, externalValueReference);
    }

    @Override
    public String toString() {
        return "FieldDefinition{" +
            "type=" + type +
            ", validation=" + validation +
            ", values=" + values +
            ", externalValueReference=" + externalValueReference +
            '}';
    }
}

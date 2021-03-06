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
    private String normalizer;
    private String formatter;

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

    public String getNormalizer() {
        return normalizer;
    }

    public void setNormalizer(String normalizer) {
        this.normalizer = normalizer;
    }

    public String getFormatter() {
        return formatter;
    }

    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldDefinition)) return false;
        FieldDefinition that = (FieldDefinition) o;
        return type == that.type &&
            Objects.equals(validation, that.validation) &&
            Objects.equals(values, that.values) &&
            Objects.equals(externalValueReference, that.externalValueReference) &&
            Objects.equals(normalizer, that.normalizer) &&
            Objects.equals(formatter, that.formatter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, validation, values, externalValueReference, normalizer, formatter);
    }

    @Override
    public String toString() {
        return "FieldDefinition{" +
            "type=" + type +
            ", validation=" + validation +
            ", values=" + values +
            ", externalValueReference=" + externalValueReference +
            ", normalizer='" + normalizer + '\'' +
            ", formatter='" + formatter + '\'' +
            '}';
    }
}

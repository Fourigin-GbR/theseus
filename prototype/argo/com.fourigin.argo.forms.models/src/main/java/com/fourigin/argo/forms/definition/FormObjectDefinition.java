package com.fourigin.argo.forms.definition;

import java.io.Serializable;
import java.util.Objects;

public class FormObjectDefinition implements Serializable {
    private static final long serialVersionUID = 8447226862026608406L;

    private String type;
    private FormObjectMapperDefinition mapper;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FormObjectMapperDefinition getMapper() {
        return mapper;
    }

    public void setMapper(FormObjectMapperDefinition mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormObjectDefinition)) return false;
        FormObjectDefinition that = (FormObjectDefinition) o;
        return Objects.equals(type, that.type) &&
            Objects.equals(mapper, that.mapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, mapper);
    }

    @Override
    public String toString() {
        return "FormObjectDefinition{" +
            "type='" + type + '\'' +
            ", mapper=" + mapper +
            '}';
    }
}

package com.fourigin.argo.models.datasource.index;

import java.io.Serializable;
import java.util.Objects;

public class FieldDefinition implements Serializable {
    private static final long serialVersionUID = -5679477000531501723L;

    private String name;
    private String path;
    private FieldType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldDefinition)) return false;
        FieldDefinition that = (FieldDefinition) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(path, that.path) &&
            type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, type);
    }

    @Override
    public String toString() {
        return "FieldDefinition{" +
            "name='" + name + '\'' +
            ", path='" + path + '\'' +
            ", type=" + type +
            '}';
    }
}

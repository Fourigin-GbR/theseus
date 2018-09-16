package com.fourigin.argo.models.datasource.index;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FieldValue implements Serializable {

    private static final long serialVersionUID = 5506494158223786807L;

/*
    {
        "name": "distance",
        "type": "NUMBER",
        "value": [
            "200",
            "250",
            "0"
        ]
    }
*/
    private String name;
    private String path;
    private FieldType type;
    private List<String> value;

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

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldValue)) return false;
        FieldValue that = (FieldValue) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(path, that.path) &&
            type == that.type &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, type, value);
    }

    @Override
    public String toString() {
        return "FieldValue{" +
            "name='" + name + '\'' +
            ", path='" + path + '\'' +
            ", type=" + type +
            ", value=" + value +
            '}';
    }
}

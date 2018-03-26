package com.fourigin.argo.models.template;

import java.io.Serializable;
import java.util.Objects;

public class TemplateVariation implements Serializable {
    private static final long serialVersionUID = -1435730032590605299L;

    public static final String DEFAULT_VARIATION_NAME = "default";

    private String id;
    private String description;
    private Type type;
    private String outputContentType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getOutputContentType() {
        return outputContentType;
    }

    public void setOutputContentType(String outputContentType) {
        this.outputContentType = outputContentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemplateVariation)) return false;
        TemplateVariation that = (TemplateVariation) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            type == that.type &&
            Objects.equals(outputContentType, that.outputContentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, type, outputContentType);
    }

    @Override
    public String toString() {
        return "TemplateVariation{" +
            "id='" + id + '\'' +
            ", description='" + description + '\'' +
            ", type=" + type +
            ", outputContentType='" + outputContentType + '\'' +
            '}';
    }
}

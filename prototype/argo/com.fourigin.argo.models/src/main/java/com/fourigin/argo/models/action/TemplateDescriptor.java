package com.fourigin.argo.models.action;

import java.io.Serializable;
import java.util.Objects;

public class TemplateDescriptor implements Serializable {
    private static final long serialVersionUID = -944628011135216423L;

    private String id;
    private String description;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateDescriptor that = (TemplateDescriptor) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description);
    }

    @Override
    public String toString() {
        return "TemplateDeclaration{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

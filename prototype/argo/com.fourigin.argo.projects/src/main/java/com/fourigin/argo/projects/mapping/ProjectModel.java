package com.fourigin.argo.projects.mapping;

import java.io.Serializable;
import java.util.Objects;

public class ProjectModel implements Serializable {
    private static final long serialVersionUID = 8908586013909141880L;

    private String id;
    private String code;
    private String name;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(o instanceof ProjectModel)) return false;
        ProjectModel that = (ProjectModel) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, description);
    }

    @Override
    public String toString() {
        return "ProjectModel{" +
            "id='" + id + '\'' +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}

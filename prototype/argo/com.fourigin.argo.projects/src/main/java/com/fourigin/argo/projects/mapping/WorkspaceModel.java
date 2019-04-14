package com.fourigin.argo.projects.mapping;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class WorkspaceModel implements Serializable {
    private static final long serialVersionUID = 1007658384112273523L;

    private String name;
    private String code;
    private String description;
    private List<ProjectModel> projects;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProjectModel> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectModel> projects) {
        this.projects = projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspaceModel)) return false;
        WorkspaceModel that = (WorkspaceModel) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(code, that.code) &&
            Objects.equals(description, that.description) &&
            Objects.equals(projects, that.projects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code, description, projects);
    }

    @Override
    public String toString() {
        return "WorkspaceModel{" +
            "name='" + name + '\'' +
            ", code='" + code + '\'' +
            ", description='" + description + '\'' +
            ", projects=" + projects +
            '}';
    }
}

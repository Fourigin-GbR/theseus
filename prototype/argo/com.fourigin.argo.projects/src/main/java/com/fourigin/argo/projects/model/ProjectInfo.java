package com.fourigin.argo.projects.model;

import java.io.Serializable;
import java.util.Objects;

public class ProjectInfo implements Serializable {
    private static final long serialVersionUID = -3551755655863568002L;

    private String id;
    private String code;
    private String name;
    private String description;
    private WorkspaceInfo workspace;

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

    public WorkspaceInfo getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceInfo workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectInfo)) return false;
        ProjectInfo that = (ProjectInfo) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(workspace, that.workspace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, description, workspace);
    }

    @Override
    public String toString() {
        return "ProjectInfo{" +
            "id='" + id + '\'' +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", workspace=" + workspace +
            '}';
    }
}

package com.fourigin.argo.projects.model;

import java.io.Serializable;
import java.util.Objects;

public class WorkspaceInfo implements Serializable {
    private static final long serialVersionUID = -1374108219997205688L;

    private String code;
    private String name;
    private String description;

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
        if (!(o instanceof WorkspaceInfo)) return false;
        WorkspaceInfo that = (WorkspaceInfo) o;
        return Objects.equals(code, that.code) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, description);
    }

    @Override
    public String toString() {
        return "WorkspaceInfo{" +
            "code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}

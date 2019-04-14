package com.fourigin.argo.projects.mapping;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class WorkspacesModel implements Serializable {
    private static final long serialVersionUID = 6562108061756712010L;

    private List<WorkspaceModel> workspaces;

    public List<WorkspaceModel> getWorkspaces() {
        return workspaces;
    }

    public void setWorkspaces(List<WorkspaceModel> workspaces) {
        this.workspaces = workspaces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspacesModel)) return false;
        WorkspacesModel that = (WorkspacesModel) o;
        return Objects.equals(workspaces, that.workspaces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workspaces);
    }

    @Override
    public String toString() {
        return "WorkspacesModel{" +
            "workspaces=" + workspaces +
            '}';
    }
}

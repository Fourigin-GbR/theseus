package com.fourigin.argo.projects;


import com.fourigin.argo.projects.model.ProjectInfo;
import com.fourigin.argo.projects.model.WorkspaceInfo;

import java.io.Flushable;

public interface ProjectResolver extends Flushable {
    ProjectInfo resolveProject(String id);

    WorkspaceInfo findWorkspace(String name);
}

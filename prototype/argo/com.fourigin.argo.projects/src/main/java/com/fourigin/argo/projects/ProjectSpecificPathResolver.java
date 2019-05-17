package com.fourigin.argo.projects;

import com.fourigin.argo.projects.model.ProjectInfo;
import com.fourigin.argo.projects.model.WorkspaceInfo;
import com.fourigin.utilities.core.PropertiesReplacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ProjectSpecificPathResolver {
    private ProjectResolver projectResolver;

    private PropertiesReplacement propertiesReplacement = new PropertiesReplacement("\\[(.+?)\\]");

    private final Logger logger = LoggerFactory.getLogger(ProjectSpecificPathResolver.class);

    public ProjectSpecificPathResolver(ProjectResolver projectResolver) {
        this.projectResolver = projectResolver;
    }

    public String resolvePath(String path, String projectId){
        if (logger.isDebugEnabled()) logger.debug("Resolving project for id '{}'", projectId);

        ProjectInfo project = projectResolver.resolveProject(projectId);
        Objects.requireNonNull(project, "No project found for id '" + projectId + "'!");

        WorkspaceInfo workspace = project.getWorkspace();
        Objects.requireNonNull(workspace, "No workspace found by project with id '" + projectId + "'!");

        String resolvedPath = propertiesReplacement.process(path,
            "workspace", workspace.getCode(),
            "project", project.getCode()
        );

        if (logger.isDebugEnabled()) logger.debug("Path '{}' resolved to '{}'", path, resolvedPath);

        return resolvedPath;
    }

    public String resolvePath(String path, String projectId, String language){
        ProjectInfo project = projectResolver.resolveProject(projectId);
        Objects.requireNonNull(project, "No project found for id '" + projectId + "'!");

        WorkspaceInfo workspace = project.getWorkspace();
        Objects.requireNonNull(workspace, "No workspace found by project with id '" + projectId + "'!");

        String resolvedPath = propertiesReplacement.process(path,
            "workspace", workspace.getCode(),
            "project", project.getCode(),
            "language", language
        );

        if (logger.isDebugEnabled()) logger.debug("Path '{}' resolved to '{}'", path, resolvedPath);

        return resolvedPath;
    }
}

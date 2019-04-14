package com.fourigin.argo.projects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.projects.mapping.ProjectModel;
import com.fourigin.argo.projects.mapping.WorkspaceModel;
import com.fourigin.argo.projects.mapping.WorkspacesModel;
import com.fourigin.argo.projects.model.ProjectInfo;
import com.fourigin.argo.projects.model.WorkspaceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class InfoFileBasedProjectResolver implements ProjectResolver {

    private String configPath;

    private ObjectMapper objectMapper;

    private Map<String, ProjectInfo> projects;

    private Set<WorkspaceInfo> workspaces;

    private final Logger logger = LoggerFactory.getLogger(InfoFileBasedProjectResolver.class);

    @Override
    public ProjectInfo resolveProject(String id) {
        if (projects == null) {
            init();
        }

        return projects.get(id);
    }

    @Override
    public WorkspaceInfo findWorkspace(String name) {
        if (projects == null) {
            init();
        }

        for (WorkspaceInfo info : workspaces) {
            if (name.equals(info.getName())) {
                return info;
            }
        }

        return null;
    }

    @Override
    public void flush() {
        init();
    }

    private void init() {
        WorkspacesModel config = readConfig();
        Objects.requireNonNull(config, "Workspaces config must not be null!");

        projects = new HashMap<>();
        workspaces = new HashSet<>();

        for (WorkspaceModel ws : config.getWorkspaces()) {
            WorkspaceInfo workspace = new WorkspaceInfo();
            workspace.setCode(ws.getCode());
            workspace.setName(ws.getName());
            workspace.setDescription(ws.getDescription());

            workspaces.add(workspace);

            for (ProjectModel pr : ws.getProjects()) {
                String id = pr.getId();

                ProjectInfo project = new ProjectInfo();
                project.setId(id);
                project.setCode(pr.getCode());
                project.setName(pr.getName());
                project.setDescription(pr.getDescription());
                project.setWorkspace(workspace);

                projects.put(id, project);
            }
        }

        if (logger.isDebugEnabled()) logger.debug("projects: \n{}", projects);
    }

    private WorkspacesModel readConfig() {
        File configFile = new File(configPath);

        if (!configFile.exists() || !configFile.canRead()) {
            if (logger.isErrorEnabled()) logger.error("Unable to read config file '{}'!", configFile.getAbsolutePath());
            return null;
        }

        try (InputStream is = new BufferedInputStream(new FileInputStream(configFile))) {
            return objectMapper.readValue(is, WorkspacesModel.class);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error reading runtime configuration file (" + configFile.getAbsolutePath() + ")!", ex);
        }
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}

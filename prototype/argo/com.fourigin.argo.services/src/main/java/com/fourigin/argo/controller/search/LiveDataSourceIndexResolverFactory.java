package com.fourigin.argo.controller.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.projects.ProjectSpecificPathResolver;
import com.fourigin.argo.repository.DataSourceIndexResolver;
import com.fourigin.argo.repository.DataSourceIndexResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveDataSourceIndexResolverFactory implements DataSourceIndexResolverFactory {

    private ObjectMapper objectMapper;

    private ProjectSpecificPathResolver pathResolver;

    private String documentRootBasePath;

    private final Logger logger = LoggerFactory.getLogger(LiveDataSourceIndexResolverFactory.class);

    @Override
    public DataSourceIndexResolver getInstance(String projectId, String language) {
        String resolvedPath = pathResolver.resolvePath(documentRootBasePath, projectId, language);

        if (logger.isDebugEnabled()) logger.debug("Instantiating a new LiveDataSourceIndexResolver instance for language '{}' and path '{}'.", language, resolvedPath);
        return new LiveDataSourceIndexResolver(resolvedPath, objectMapper);
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setDocumentRootBasePath(String documentRootBasePath) {
        this.documentRootBasePath = documentRootBasePath;
    }

    public void setPathResolver(ProjectSpecificPathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }
}

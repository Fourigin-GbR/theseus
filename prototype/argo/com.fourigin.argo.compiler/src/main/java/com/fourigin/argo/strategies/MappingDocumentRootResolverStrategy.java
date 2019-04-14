package com.fourigin.argo.strategies;

import com.fourigin.argo.projects.ProjectSpecificPathResolver;

public class MappingDocumentRootResolverStrategy implements DocumentRootResolverStrategy {

    private ProjectSpecificPathResolver pathResolver;

    private String documentRootBasePath;

    public MappingDocumentRootResolverStrategy(ProjectSpecificPathResolver pathResolver, String documentRootBasePath) {
        this.pathResolver = pathResolver;
        this.documentRootBasePath = documentRootBasePath;
    }

    @Override
    public String resolveDocumentRoot(String projectId, String language) {
        return pathResolver.resolvePath(documentRootBasePath, projectId, language);

//        Map<String, Map<String, String>> docRoots = projectSpecificConfiguration.getDocumentRoots();
//
//        Map<String, String> projectRoots = docRoots.get(projectId);
//        Objects.requireNonNull(projectRoots, "No project specific document root specified for '" + projectId + "'!");
//
//        return projectRoots.get(language);
    }
}

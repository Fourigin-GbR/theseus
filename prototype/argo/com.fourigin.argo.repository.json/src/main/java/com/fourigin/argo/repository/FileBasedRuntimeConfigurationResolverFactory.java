package com.fourigin.argo.repository;

import com.fourigin.argo.projects.ProjectSpecificPathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class FileBasedRuntimeConfigurationResolverFactory implements RuntimeConfigurationResolverFactory {

    private String basePath;

    private ProjectSpecificPathResolver pathResolver;

    private ConcurrentHashMap<String, RuntimeConfigurationResolver> cache = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(FileBasedRuntimeConfigurationResolverFactory.class);

    public FileBasedRuntimeConfigurationResolverFactory(ProjectSpecificPathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    @Override
    public RuntimeConfigurationResolver getInstance(String project, String language) {
        String cacheKey = project + ':' + language;

        RuntimeConfigurationResolver resolver = cache.get(cacheKey);
        if(resolver != null){
            if (logger.isDebugEnabled()) logger.debug("Using cached RuntimeConfigurationResolver instance for key '{}'.", cacheKey);
            return resolver;
        }

        String path = pathResolver.resolvePath(basePath, project, language);

        if (logger.isDebugEnabled()) logger.debug("Instantiating a new RuntimeConfigurationResolver instance for key '{}' and path '{}'.", cacheKey, path);
        resolver = new FileBasedRuntimeConfigurationResolver(project, path);

        cache.putIfAbsent(cacheKey, resolver);

        return resolver;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}

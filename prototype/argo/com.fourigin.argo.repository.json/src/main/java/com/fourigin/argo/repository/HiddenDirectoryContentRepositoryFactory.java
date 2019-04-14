package com.fourigin.argo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.projects.ProjectSpecificPathResolver;
import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class HiddenDirectoryContentRepositoryFactory implements ContentRepositoryFactory {

    private String basePath;

    private PageInfoTraversingStrategy defaultTraversingStrategy;

    private ProjectSpecificPathResolver pathResolver;

    private ObjectMapper objectMapper;

    private ConcurrentHashMap<String, HiddenDirectoryContentRepository> cache = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(HiddenDirectoryContentRepositoryFactory.class);

    @Override
    public ContentRepository getInstance(String projectId, String language) {
        String cacheKey = projectId + ":" + language;

        HiddenDirectoryContentRepository repository = cache.get(cacheKey);
        if (repository != null) {
            if (logger.isDebugEnabled())
                logger.debug("Using cached ContentRepository instance for key '{}'.", cacheKey);
            return repository;
        }

        String path = pathResolver.resolvePath(basePath, projectId, language);

        if (logger.isDebugEnabled())
            logger.debug("Instantiating a new ContentRepository instance for project '{}', language '{}' and path '{}'.", projectId, language, path);
        repository = new HiddenDirectoryContentRepository();


        repository.setContentRoot(path);
        repository.setDefaultTraversingStrategy(defaultTraversingStrategy);
        repository.setObjectMapper(objectMapper);

        cache.putIfAbsent(cacheKey, repository);

        return repository;
    }

    public void setPathResolver(ProjectSpecificPathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setDefaultTraversingStrategy(PageInfoTraversingStrategy defaultTraversingStrategy) {
        this.defaultTraversingStrategy = defaultTraversingStrategy;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}

package com.fourigin.argo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;
import com.fourigin.utilities.core.PropertiesReplacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class HiddenDirectoryContentRepositoryFactory implements ContentRepositoryFactory {

    private String basePath;

    private String keyName;

    private PageInfoTraversingStrategy defaultTraversingStrategy;

    private ObjectMapper objectMapper;

    private PropertiesReplacement propertiesReplacement = new PropertiesReplacement("\\[(.+?)\\]");

    private ConcurrentHashMap<String, HiddenDirectoryContentRepository> cache = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(HiddenDirectoryContentRepositoryFactory.class);

    @Override
    public ContentRepository getInstance(String customer, String key) {
        String cacheKey = customer + ":" + key;

        HiddenDirectoryContentRepository repository = cache.get(cacheKey);
        if(repository != null){
            if (logger.isDebugEnabled()) logger.debug("Using cached ContentRepository instance for key '{}'.", cacheKey);
            return repository;
        }

        if (logger.isDebugEnabled()) logger.debug("Resolving content root path from pattern '{}' with '{}'='{}' and customer='{}'", basePath, keyName, key, customer);
        String path = propertiesReplacement.process(basePath, "customer", customer, keyName, key);

        if (logger.isDebugEnabled()) logger.debug("Instantiating a new ContentRepository instance for key '{}' and path '{}'.", key, path);
        repository = new HiddenDirectoryContentRepository();

        repository.setContentRoot(path);
        repository.setDefaultTraversingStrategy(defaultTraversingStrategy);
        repository.setObjectMapper(objectMapper);

        cache.putIfAbsent(cacheKey, repository);

        return repository;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setDefaultTraversingStrategy(PageInfoTraversingStrategy defaultTraversingStrategy) {
        this.defaultTraversingStrategy = defaultTraversingStrategy;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}

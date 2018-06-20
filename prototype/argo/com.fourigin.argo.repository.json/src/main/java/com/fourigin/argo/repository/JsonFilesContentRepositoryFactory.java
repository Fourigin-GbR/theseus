package com.fourigin.argo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;
import com.fourigin.utilities.core.PropertiesReplacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class JsonFilesContentRepositoryFactory implements ContentRepositoryFactory {

    private String basePath;

    private String keyName;

    private String siteStructureFileName;

    private String directoryInfoFileName;

    private PageInfoTraversingStrategy defaultTraversingStrategy;

    private ObjectMapper objectMapper;

    private PropertiesReplacement propertiesReplacement = new PropertiesReplacement();

    private ConcurrentHashMap<String, JsonFilesContentRepository> cache = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(JsonFilesContentRepositoryFactory.class);

    @Override
    public ContentRepository getInstance(String key) {
        JsonFilesContentRepository repository = cache.get(key);
        if(repository != null){
            if (logger.isDebugEnabled()) logger.debug("Using cached ContentRepository instance for key '{}'.", key);
            return repository;
        }

        String path = propertiesReplacement.process(basePath, keyName, key);

        if (logger.isDebugEnabled()) logger.debug("Instantiating a new ContentRepository instance for key '{}' and path '{}'.", key, path);
        repository = new JsonFilesContentRepository();

        repository.setContentRoot(path);
        repository.setSiteStructureFileName(siteStructureFileName);
        repository.setDirectoryInfoFileName(directoryInfoFileName);
        repository.setDefaultTraversingStrategy(defaultTraversingStrategy);
        repository.setObjectMapper(objectMapper);

        cache.putIfAbsent(key, repository);

        return repository;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setSiteStructureFileName(String siteStructureFileName) {
        this.siteStructureFileName = siteStructureFileName;
    }

    public void setDirectoryInfoFileName(String directoryInfoFileName) {
        this.directoryInfoFileName = directoryInfoFileName;
    }

    public void setDefaultTraversingStrategy(PageInfoTraversingStrategy defaultTraversingStrategy) {
        this.defaultTraversingStrategy = defaultTraversingStrategy;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}

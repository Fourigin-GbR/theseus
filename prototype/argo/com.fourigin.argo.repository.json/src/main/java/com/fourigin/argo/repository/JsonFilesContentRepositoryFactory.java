package com.fourigin.argo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;
import com.fourigin.utilities.core.PropertiesReplacement;

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

    @Override
    public ContentRepository getInstance(String key) {
        JsonFilesContentRepository repository = cache.get(key);
        if(repository != null){
            return repository;
        }

        String path = propertiesReplacement.process(basePath, keyName, key);

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

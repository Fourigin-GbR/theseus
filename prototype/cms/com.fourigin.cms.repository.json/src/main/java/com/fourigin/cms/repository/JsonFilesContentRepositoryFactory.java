package com.fourigin.cms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.cms.repository.strategies.PageInfoTraversingStrategy;
import com.fourigin.utilities.core.PropertiesReplacement;

public class JsonFilesContentRepositoryFactory implements ContentRepositoryFactory {

    private String basePath;

    private String keyName;

    private String siteStructureFileName;

    private String directoryInfoFileName;

    private PageInfoTraversingStrategy defaultTraversingStrategy;

    private ObjectMapper objectMapper;

    private PropertiesReplacement propertiesReplacement = new PropertiesReplacement();

    @Override
    public ContentRepository getInstance(String key) {
        String path = propertiesReplacement.process(basePath, keyName, key);

        JsonFilesContentRepository repository = new JsonFilesContentRepository();

        repository.setContentRoot(path);
        repository.setSiteStructureFileName(siteStructureFileName);
        repository.setDirectoryInfoFileName(directoryInfoFileName);
        repository.setDefaultTraversingStrategy(defaultTraversingStrategy);
        repository.setObjectMapper(objectMapper);

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
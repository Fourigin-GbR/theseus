package com.fourigin.argo.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.content.elements.mapping.ContentPageModule;
import com.fourigin.argo.repository.JsonFilesContentRepositoryFactory;
import com.fourigin.argo.repository.model.mapping.JsonInfoModule;
import com.fourigin.argo.repository.strategies.DefaultPageInfoTraversingStrategy;
import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentPageRepositoryConfiguration {

    @Bean
    public JsonFilesContentRepositoryFactory jsonFilesContentRepositoryFactory(
        @Value("${content-page-repository.root-path}") String basePath,
        @Value("${content-page-repository.key-name}") String keyName,
        @Value("${content-page-repository.site-structure-file-name}") String siteStructureFileName,
        @Value("${content-page-repository.directory-info-file-name}") String directoryInfoFileName,
        @Autowired PageInfoTraversingStrategy defaultTraversingStrategy
    ) {
        JsonFilesContentRepositoryFactory factory = new JsonFilesContentRepositoryFactory();

        factory.setBasePath(basePath);
        factory.setKeyName(keyName);
        factory.setSiteStructureFileName(siteStructureFileName);
        factory.setDirectoryInfoFileName(directoryInfoFileName);
        factory.setDefaultTraversingStrategy(defaultTraversingStrategy);
        factory.setObjectMapper(objectMapper());

        return factory;
    }

    @Bean
    public PageInfoTraversingStrategy traversingStrategy(){
        return new DefaultPageInfoTraversingStrategy();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new ContentPageModule());
        objectMapper.registerModule(new JsonInfoModule());
        return objectMapper;
    }
}

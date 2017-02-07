package com.fourigin.cms.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fourigin.cms.models.content.elements.mapping.ContentPageModule;
import com.fourigin.cms.repository.ContentPageRepository;
import com.fourigin.cms.repository.JsonFileContentPageRepository;
import com.fourigin.cms.repository.JsonFilesContentRepositoryFactory;
import com.fourigin.cms.repository.strategies.DefaultPageInfoTraversingStrategy;
import com.fourigin.cms.repository.strategies.PageInfoTraversingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

//@Configuration
public class ContentPageRepositoryConfiguration {

    @Bean
    @Deprecated
    public ContentPageRepository contentPageRepository(
      @Value("${content-page-repository.root-path}") String contentRoot
    ) {
        return new JsonFileContentPageRepository.Builder()
          .contentRoot(contentRoot)
          .objectMapper(objectMapper())
          .build();
    }

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
        objectMapper.setDateFormat(new ISO8601DateFormat());
        objectMapper.registerModule(new ContentPageModule());
        return objectMapper;
    }

}

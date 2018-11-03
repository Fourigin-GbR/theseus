package com.fourigin.argo.forms.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.content.elements.mapping.ContentPageModule;
import com.fourigin.argo.repository.DirectoryContentBasedTemplateResolver;
import com.fourigin.argo.repository.FileBasedRuntimeConfigurationResolverFactory;
import com.fourigin.argo.repository.HiddenDirectoryContentRepositoryFactory;
import com.fourigin.argo.repository.TemplateResolver;
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
    public HiddenDirectoryContentRepositoryFactory contentRepositoryFactory(
        @Value("${content-page-repository.root-path}") String basePath,
        @Value("${content-page-repository.key-name}") String keyName,
        @Autowired PageInfoTraversingStrategy defaultTraversingStrategy
    ) {
        HiddenDirectoryContentRepositoryFactory factory = new HiddenDirectoryContentRepositoryFactory();

        factory.setBasePath(basePath);
        factory.setKeyName(keyName);
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

        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        return objectMapper;
    }

    @Bean
    public ContentPageModule contentPageModule(){
        return new ContentPageModule();
    }

    @Bean
    public JsonInfoModule jsonInfoModule(){
        return new JsonInfoModule();
    }

    @Bean
    public TemplateResolver templateResolver(
        @Value("${template.engine.thymeleaf.base}") String templateBasePath
    ) {
        return new DirectoryContentBasedTemplateResolver(templateBasePath, objectMapper());
    }

    @Bean
    public FileBasedRuntimeConfigurationResolverFactory runtimeConfigurationResolverFactory(
        @Value("${content-page-repository.root-path}") String basePath,
        @Value("${content-page-repository.key-name}") String keyName
    ){
        FileBasedRuntimeConfigurationResolverFactory factory = new FileBasedRuntimeConfigurationResolverFactory();

        factory.setBasePath(basePath);
        factory.setKeyName(keyName);

        return factory;
    }
}

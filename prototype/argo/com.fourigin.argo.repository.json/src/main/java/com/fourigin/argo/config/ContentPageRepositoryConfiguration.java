package com.fourigin.argo.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.action.mapping.ActionModule;
import com.fourigin.argo.models.content.elements.mapping.ContentPageModule;
import com.fourigin.argo.projects.ProjectSpecificPathResolver;
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

    private ProjectSpecificPathResolver pathResolver;

    public ContentPageRepositoryConfiguration(ProjectSpecificPathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    @Bean
    public HiddenDirectoryContentRepositoryFactory contentRepositoryFactory(
        @Value("${content-page-repository.root-path}") String basePath,
        @Autowired PageInfoTraversingStrategy defaultTraversingStrategy
    ) {
        HiddenDirectoryContentRepositoryFactory factory = new HiddenDirectoryContentRepositoryFactory();

        factory.setBasePath(basePath);
        factory.setPathResolver(pathResolver);
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
        objectMapper.registerModule(new ActionModule());

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
        return new DirectoryContentBasedTemplateResolver(templateBasePath, objectMapper(), pathResolver);
    }

    @Bean
    public FileBasedRuntimeConfigurationResolverFactory runtimeConfigurationResolverFactory(
        @Value("${content-page-repository.root-path}") String basePath
    ){
        FileBasedRuntimeConfigurationResolverFactory factory = new FileBasedRuntimeConfigurationResolverFactory(pathResolver);

        factory.setBasePath(basePath);

        return factory;
    }
}

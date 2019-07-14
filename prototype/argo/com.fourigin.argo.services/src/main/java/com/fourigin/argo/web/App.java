package com.fourigin.argo.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.config.ContentPageRepositoryConfiguration;
import com.fourigin.argo.controller.search.LiveDataSourceIndexResolverFactory;
import com.fourigin.argo.forms.config.ProjectSpecificConfiguration;
import com.fourigin.argo.projects.ProjectSpecificPathResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@EnableAutoConfiguration
@ComponentScan(value = {
    "com.fourigin.argo.web",
    "com.fourigin.argo.controller",
    "com.fourigin.argo.config",
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ContentPageRepositoryConfiguration.class)
})
@SpringBootApplication
@EnableCaching
public class App {

    @Value("${document-root.base}")
    private String documentRootBase;

    private ProjectSpecificPathResolver projectSpecificPathResolver;

    public App(ProjectSpecificPathResolver projectSpecificPathResolver) {
        this.projectSpecificPathResolver = projectSpecificPathResolver;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.addListeners(
                new ApplicationPidFileWriter()  // DEFAULT: application.pid
        );
        app.run(args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        return objectMapper;
    }

    @Bean
    public LiveDataSourceIndexResolverFactory dataSourceIndexResolverFactory() {
        LiveDataSourceIndexResolverFactory factory = new LiveDataSourceIndexResolverFactory();

        factory.setObjectMapper(objectMapper());
        factory.setPathResolver(projectSpecificPathResolver);
        factory.setDocumentRootBasePath(documentRootBase);

        return factory;
    }

//    @Bean
//    public ProjectSpecificPathResolver pathResolver(){
//        return new ProjectSpecificPathResolver(projectResolver());
//    }
//
//    @Bean
//    public InfoFileBasedProjectResolver projectResolver(){
//        InfoFileBasedProjectResolver projectResolver = new InfoFileBasedProjectResolver();
//
//        projectResolver.setConfigPath(workspacesBasePath);
//        projectResolver.setObjectMapper(objectMapper());
//
//        return projectResolver;
//    }

    @Bean
    @ConfigurationProperties(prefix = "project")
    public ProjectSpecificConfiguration projectSpecificConfiguration() {
        return new ProjectSpecificConfiguration();
    }
}

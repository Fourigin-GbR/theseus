package com.fourigin.argo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.projects.InfoFileBasedProjectResolver;
import com.fourigin.argo.projects.ProjectSpecificPathResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectsConfiguration {

    @Value("${project-repository.config-path}")
    private String workspacesConfigPath;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public ProjectSpecificPathResolver pathResolver(){
        return new ProjectSpecificPathResolver(projectResolver());
    }

    @Bean
    public InfoFileBasedProjectResolver projectResolver(){
        InfoFileBasedProjectResolver projectResolver = new InfoFileBasedProjectResolver();

        projectResolver.setConfigPath(workspacesConfigPath);
        projectResolver.setObjectMapper(objectMapper);

        return projectResolver;
    }
}

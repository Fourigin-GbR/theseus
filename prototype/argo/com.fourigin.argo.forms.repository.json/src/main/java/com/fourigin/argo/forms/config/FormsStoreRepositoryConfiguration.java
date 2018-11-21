package com.fourigin.argo.forms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.JsonFilesBasedFormsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class FormsStoreRepositoryConfiguration {

    @Bean
    public JsonFilesBasedFormsRepository jsonFilesBasedFormsStoreRepository(
        @Value("${forms-store-repository.root-path}") File basePath,
        @Autowired ObjectMapper objectMapper
    ) {
        return new JsonFilesBasedFormsRepository(basePath, objectMapper);
    }
}

package com.fourigin.argo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.ExternalValueResolver;
import com.fourigin.argo.forms.JsonFilesBasedFormsStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Set;

@Configuration
public class FormsStoreRepositoryConfiguration {

    @Bean
    public JsonFilesBasedFormsStoreRepository jsonFilesBasedFormsStoreRepository(
        @Value("${forms-store-repository.root-path}") File basePath,
        @Autowired ObjectMapper objectMapper,
        @Autowired(required = false) Set<ExternalValueResolver> externalValueResolvers
    ) {
        JsonFilesBasedFormsStoreRepository result = new JsonFilesBasedFormsStoreRepository(basePath, objectMapper);
        result.setExternalValueResolvers(externalValueResolvers);
        return result;
    }
}

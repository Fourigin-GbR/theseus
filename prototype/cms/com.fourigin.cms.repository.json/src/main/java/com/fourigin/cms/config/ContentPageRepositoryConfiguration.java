package com.fourigin.cms.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fourigin.cms.repository.ContentPageModule;
import com.fourigin.cms.repository.ContentPageRepository;
import com.fourigin.cms.repository.JsonFileContentPageRepository;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentPageRepositoryConfiguration {

    @Bean
    public ContentPageRepository contentPageRepository(
      @Value("${content-page-repository.root-path}") String contentRoot
    ) {
        return new JsonFileContentPageRepository.Builder()
          .contentRoot(contentRoot)
          .objectMapper(objectMapper())
          .build();
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

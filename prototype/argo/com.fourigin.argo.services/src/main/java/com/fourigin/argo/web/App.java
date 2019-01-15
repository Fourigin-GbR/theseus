package com.fourigin.argo.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.controller.search.LiveDataSourceIndexResolverFactory;
import com.fourigin.argo.forms.config.CustomerSpecificConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan({
    "com.fourigin.argo.web",
    "com.fourigin.argo.controller"
})
@SpringBootApplication
@EnableCaching
public class App {

    private static final String APP_NAME = "argo-services";

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.addListeners(
            new ApplicationPidFileWriter(APP_NAME + ".pid")
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
        factory.setCustomerSpecificConfiguration(customerSpecificConfiguration());

        return factory;
    }

    @Bean
    @ConfigurationProperties(prefix = "customer")
    public CustomerSpecificConfiguration customerSpecificConfiguration(){
        return new CustomerSpecificConfiguration();
    }
}

package com.fourigin.hera.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"com.fourigin.hera", "com.fourigin.hera.web"})
@SpringBootApplication
public class App {

    private static final String APP_NAME = "hera";

    @Value("${hera.context-path}")
    private String contextPath;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.addListeners(
            new ApplicationPidFileWriter(APP_NAME + ".pid")
        );
        app.run(args);
    }

    @Bean
    public SimpleUrlHandlerMapping dynamicHeraServletMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MAX_VALUE - 2);

        Properties urlProperties = new Properties();
        urlProperties.put(contextPath + "/**", "heraController");

        mapping.setMappings(urlProperties);

        return mapping;
    }
}

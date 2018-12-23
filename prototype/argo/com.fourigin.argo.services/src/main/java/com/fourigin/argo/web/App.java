package com.fourigin.argo.web;

import com.fourigin.argo.controller.search.LiveDataSourceIndexResolver;
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

    private static final String APP_NAME = "argo-service";

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.addListeners(
            new ApplicationPidFileWriter(APP_NAME + ".pid")
        );
        app.run(args);
    }

    @Bean
    @ConfigurationProperties(prefix = "customer")
    public CustomerSpecificConfiguration customerSpecificConfiguration(){
        return new CustomerSpecificConfiguration();
    }

    @Bean
    public LiveDataSourceIndexResolver dataSourceIndexResolver(){
        return new LiveDataSourceIndexResolver();
    }
}

package com.fourigin.argo.forms.dashboard;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.forms.customer.payment.mapping.PaymentModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan({
    "com.fourigin.argo.forms.config",
    "com.fourigin.argo.forms.dashboard"
})
@SpringBootApplication
@EnableCaching
public class App {

    private static final String APP_NAME = "dashboard";

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
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.registerModule(new PaymentModule());
        return objectMapper;
    }
}

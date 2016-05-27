package com.fourigin.apps.theseus.prototype;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import com.fourigin.theseus.models.Classification;
import com.fourigin.theseus.models.ClassificationType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"com.fourigin.apps.theseus.prototype", "com.fourigin.logger"})
@SpringBootApplication
public class Prototype extends WebMvcConfigurerAdapter {

    private static final String APP_NAME = "theseus";

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Prototype.class);
        app.addListeners(
          new ApplicationPidFileWriter(APP_NAME + ".pid")
        );
        app.run(args);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @Bean
    public LocaleResolver localeResolver() {

        return new AbstractLocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                String requestUrl = request.getRequestURL().toString();
                String language = requestUrl.contains(".de/")?"de":"en";
                return new Locale(language);
            }

            @Override
            public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
                // nada!
            }
        };

    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource(){
        ReloadableResourceBundleMessageSource result = new ReloadableResourceBundleMessageSource();

        result.setBasename("classpath:messages");
        result.setCacheSeconds(60);
        result.setDefaultEncoding(StandardCharsets.UTF_8.name());

        return result;
    }

    @Bean
    public ModelObjectRepositoryStub modelObjectRepositoryStub(){
        ModelObjectRepositoryStub result = new ModelObjectRepositoryStub();

        Classification.Builder classificationBuilder = new Classification.Builder();

        result.create(classificationBuilder.id("c140").typeCode("model").description("Captiva (C140)").build());
        result.create(classificationBuilder.id("1yy").typeCode("model").description("Corvette Stingray (1YY)").build());
        result.create(classificationBuilder.id("1yz").typeCode("model").description("Corvette Z06 (1YZ)").build());
        result.create(classificationBuilder.id("ls").typeCode("trim").description("LS").build());
        result.create(classificationBuilder.id("lt").typeCode("trim").description("LT").build());
        result.create(classificationBuilder.id("1.8").typeCode("version").description("1.8").build());

        ClassificationType.Builder classificationTypeBuilder = new ClassificationType.Builder();

        result.create(classificationTypeBuilder.id("model").description("Carline").build());
        result.create(classificationTypeBuilder.id("trim").description("Trim level").build());
        result.create(classificationTypeBuilder.id("version").description("Version").build());

        return result;
    }
}

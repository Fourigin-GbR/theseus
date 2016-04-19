package com.fourigin.apps.theseus.prototype;

import java.util.Locale;

import com.fourigin.theseus.models.Classification;
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
@ComponentScan
@SpringBootApplication
public class Prototype extends WebMvcConfigurerAdapter {

    private static final String APP_NAME = "theseus";

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Prototype.class);
        app.addListeners(
          new ApplicationPidFileWriter(APP_NAME+".pid")
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
        result.setDefaultEncoding("UTF-8");

        return result;
    }

    @Bean
    public ModelObjectRepositoryStub modelObjectRepositoryStub(){
        ModelObjectRepositoryStub result = new ModelObjectRepositoryStub();

        Classification.Builder builder = new Classification.Builder();

        result.create(builder.id("c140").typeCode("model").description("Captiva (C140)").build());
        result.create(builder.id("1yy").typeCode("model").description("Corvette Stingray (1YY)").build());
        result.create(builder.id("1yz").typeCode("model").description("Corvette Z06 (1YZ)").build());
        result.create(builder.id("ls").typeCode("trim").description("LS").build());
        result.create(builder.id("lt").typeCode("trim").description("LT").build());
        result.create(builder.id("1.8").typeCode("version").description("1.8").build());

        return result;
    }
}

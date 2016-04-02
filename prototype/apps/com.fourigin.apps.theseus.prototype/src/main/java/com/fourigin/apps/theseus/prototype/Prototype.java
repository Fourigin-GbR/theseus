package com.fourigin.apps.theseus.prototype;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
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
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.AbstractLocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Configuration
@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
public class Prototype extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Prototype.class, args);
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

//        SessionLocaleResolver slr = new SessionLocaleResolver();
//        slr.setDefaultLocale(Locale.US);
//        return slr;
    }

//    @Bean
//    public LocaleChangeInterceptor localeChangeInterceptor() {
//        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
//        lci.setParamName("lang");
//        return lci;
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(localeChangeInterceptor());
//    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource(){
        ReloadableResourceBundleMessageSource result = new ReloadableResourceBundleMessageSource();

        result.setBasename("classpath:messages");
        result.setCacheSeconds(60);
        result.setDefaultEncoding("UTF-8");

        return result;
    }
}

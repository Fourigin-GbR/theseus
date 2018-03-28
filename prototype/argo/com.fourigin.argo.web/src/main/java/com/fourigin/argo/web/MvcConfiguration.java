package com.fourigin.argo.web;

import com.fourigin.argo.interceptors.FlushRepositoriesInterceptor;
import com.fourigin.argo.repository.ContentRepositoryFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.nio.charset.StandardCharsets;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    @Value("${template.engine.thymeleaf.internal.base}")
    private String internalTemplateBasePath;

    private ContentRepositoryFactory contentRepositoryFactory;

    public MvcConfiguration(ContentRepositoryFactory contentRepositoryFactory) {
        this.contentRepositoryFactory = contentRepositoryFactory;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(new FlushRepositoriesInterceptor(contentRepositoryFactory));
    }

    private SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        templateEngine.setTemplateResolver(fileTemplateResolver());

        return templateEngine;
    }

    private FileTemplateResolver fileTemplateResolver() {
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        String prefix = internalTemplateBasePath;
        if (!prefix.endsWith("/")) {
            prefix += "/";
        }

        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCacheable(false);
        templateResolver.setCacheTTLMs(1L);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return templateResolver;
    }

    @Bean
    public ViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();

        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return viewResolver;
    }
}

package com.fourigin.argo.template.engine;

import com.fourigin.argo.projects.ProjectSpecificPathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.nio.charset.StandardCharsets;

public class InternalTemplateEngineFactory {
    private ProjectSpecificPathResolver pathResolver;

    private String templateBasePath;

    private final Logger logger = LoggerFactory.getLogger(InternalTemplateEngineFactory.class);

    public TemplateEngine getInstance(String projectId){
        if (logger.isInfoEnabled()) logger.info("Using pathResolver: {} and templateBasePath: '{}'", pathResolver, templateBasePath);

        TemplateEngine templateEngine = new TemplateEngine();

        FileTemplateResolver templateResolver = new FileTemplateResolver();

        String prefix = pathResolver.resolvePath(templateBasePath, projectId);
        if (!prefix.endsWith("/")) {
            prefix += "/";
        }
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCacheable(false);
        templateResolver.setCacheTTLMs(1L);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());

        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }

    public void setPathResolver(ProjectSpecificPathResolver pathResolver) {
        if (logger.isInfoEnabled()) logger.info("Setting pathResolver {}", pathResolver);
        this.pathResolver = pathResolver;
    }

    public void setTemplateBasePath(String templateBasePath) {
        if (logger.isInfoEnabled()) logger.info("Setting templateBasePath {}", templateBasePath);
        this.templateBasePath = templateBasePath;
    }

    @Override
    public String toString() {
        return "InternalTemplateEngineFactory{" +
            "pathResolver=" + pathResolver +
            ", templateBasePath='" + templateBasePath + '\'' +
            '}';
    }
}

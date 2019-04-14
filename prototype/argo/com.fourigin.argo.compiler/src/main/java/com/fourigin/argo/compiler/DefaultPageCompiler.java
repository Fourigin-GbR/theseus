package com.fourigin.argo.compiler;

import com.fourigin.argo.compiler.datasource.DataSourcesResolver;
import com.fourigin.argo.compiler.processor.ContentPageProcessor;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.config.RuntimeConfiguration;
import com.fourigin.argo.models.content.config.RuntimeConfigurations;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.models.template.TemplateReference;
import com.fourigin.argo.models.template.TemplateVariation;
import com.fourigin.argo.models.template.Type;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.RuntimeConfigurationResolver;
import com.fourigin.argo.repository.TemplateResolver;
import com.fourigin.argo.strategies.CompilerOutputStrategy;
import com.fourigin.argo.template.engine.PageInfoAwareTemplateEngine;
import com.fourigin.argo.template.engine.ProcessingMode;
import com.fourigin.argo.template.engine.SiteAttributesAwareTemplateEngine;
import com.fourigin.argo.template.engine.ArgoTemplateEngine;
import com.fourigin.argo.template.engine.ArgoTemplateEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultPageCompiler implements PageCompiler {
    private String project;

    private String language;

    private ContentRepository contentRepository;

    private TemplateResolver templateResolver;

    private ArgoTemplateEngineFactory argoTemplateEngineFactory;

    private DataSourcesResolver dataSourcesResolver;

    private List<ContentPageProcessor> contentPageProcessors;

    private List<CompilerInterceptor> compilerInterceptors;

    private RuntimeConfigurationResolver runtimeConfigurationResolver;

    private final Logger logger = LoggerFactory.getLogger(DefaultPageCompiler.class);

    private static final Map<String, String> CONTENT_TYPE_EXTENSION_MAPPING = new HashMap<>();

    static {
        CONTENT_TYPE_EXTENSION_MAPPING.put("text/html", ".html");
    }

    public DefaultPageCompiler(String project, String language) {
        this.project = project;
        this.language = language;

        if (logger.isInfoEnabled()) logger.info("Initializing PageCompiler for project '{}' and language '{}'", project, language);
    }

    public ContentPage prepareContent(PageInfo pageInfo, ProcessingMode processingMode) {
        String pageName = pageInfo.getName();
        if (logger.isInfoEnabled()) logger.info("Preparing content page '{}'.", pageName);

        ContentPage contentPage = contentRepository.retrieve(pageInfo);
        if (contentPage == null) {
            throw new IllegalStateException("No ContentPage assigned to PageInfo " + pageInfo);
        }
        if (logger.isDebugEnabled()) logger.debug("Content page: {}", contentPage.getId());

        ContentPage result = contentPage;

        boolean maybeChanged = false;

        // resolve all data sources
        if (dataSourcesResolver != null) {
            if (logger.isDebugEnabled()) logger.debug("Resolving data sources of '{}'", pageName);
            result = dataSourcesResolver.resolve(pageInfo, contentRepository, contentPage, project, language);
            maybeChanged = true;
        }

        // process content
        if (contentPageProcessors != null) {
            if (logger.isDebugEnabled()) logger.debug("Applying content page processors");

            for (ContentPageProcessor contentPageProcessor : contentPageProcessors) {
                contentPageProcessor.process(project, language, pageInfo, processingMode, result);
            }

            maybeChanged = true;
        }

        if (maybeChanged && !contentPage.equals(result)) {
            if (logger.isDebugEnabled()) logger.debug("Updating modified content page");
            contentRepository.update(pageInfo, result);
        } else {
            if (logger.isDebugEnabled())
                logger.debug("No dataSource changes detected, the content page is unchanged.");
        }

        if (runtimeConfigurationResolver != null) {
            RuntimeConfigurations configurations = result.getConfigurations();
            if (configurations != null) {
                Set<String> names = configurations.getNames();

                Set<RuntimeConfiguration> configs = new HashSet<>();
                for (String configName : names) {
                    if (logger.isDebugEnabled())
                        logger.debug("Resolving runtime configuration for name '{}'", configName);

                    RuntimeConfiguration config = runtimeConfigurationResolver.resolveConfiguration(configName);
                    if (config != null) {
                        if (logger.isDebugEnabled()) logger.debug("Applying resolved configuration {}", config);
                        configs.add(config);
                    } else {
                        if (logger.isWarnEnabled())
                            logger.warn("Unable to load runtime configuration for name '{}'!", configName);
                    }
                }

                configurations.setConfigurations(configs);
                result.setConfigurations(configurations);
            } else {
                if (logger.isDebugEnabled()) logger.debug("No runtime configuration requested from the content page.");
            }
        }

        return result;
    }

    @Override
    public String compile(String path, PageInfo pageInfo, ProcessingMode processingMode, CompilerOutputStrategy outputStrategy) {
        String pageName = pageInfo.getName();
        if (logger.isInfoEnabled()) logger.info("Compiling page '{}'.", pageName);

        TemplateReference templateReference = pageInfo.getTemplateReference();
        if (templateReference == null) {
            throw new IllegalStateException("No TemplateReference defined for PageInfo " + pageInfo);
        }
        if (logger.isDebugEnabled()) logger.debug("Template reference: {}", templateReference);

        String templateId = templateReference.getTemplateId();
        Template template = templateResolver.retrieve(project, templateId);
        if (template == null) {
            throw new IllegalStateException("No template found for id '" + templateId + "'!");
        }
        if (logger.isDebugEnabled()) logger.debug("Template: {}", templateId);

        String currentTemplateRevision = template.getRevision();
        String lastTemplateRevision = templateReference.getRevision();
        if (logger.isDebugEnabled())
            logger.debug("Last revision: {},\ncurrent revision: {}", lastTemplateRevision, currentTemplateRevision);

        Collection<TemplateVariation> variations = template.getVariations();
        if (variations == null || variations.isEmpty()) {
            throw new IllegalStateException("No template variations defined in template '" + templateId + "'!");
        }
        String templateVariationId = templateReference.getVariationId();
        if (logger.isDebugEnabled()) logger.debug("Template variation id: '{}'", templateVariationId);

        TemplateVariation templateVariation = null;
        for (TemplateVariation variation : variations) {
            if (templateVariationId.equals(variation.getId())) {
                templateVariation = variation;
                break;
            }
        }
        if (templateVariation == null) {
            throw new IllegalStateException("No template variation found in template '" + templateId + "' for id '" + templateVariationId + "'!");
        }
        if (logger.isDebugEnabled()) logger.debug("Template variation: '{}'", templateVariation);

        Type templateType = templateVariation.getType();
        if (logger.isDebugEnabled()) logger.debug("Template type: {}", templateType);

        ArgoTemplateEngine argoTemplateEngine = argoTemplateEngineFactory.getInstance(templateType);
        if (argoTemplateEngine == null) {
            throw new IllegalStateException("Unsupported template engine type '" + templateType + "'!");
        }
        
        // initialize the template engine
        argoTemplateEngine.setProject(project);
        argoTemplateEngine.setLanguage(language);
        argoTemplateEngine.setPath(path);

        if (PageInfoAwareTemplateEngine.class.isAssignableFrom(argoTemplateEngine.getClass())) {
            ((PageInfoAwareTemplateEngine) argoTemplateEngine).setPageInfo(pageInfo);
        }
        if (SiteAttributesAwareTemplateEngine.class.isAssignableFrom(argoTemplateEngine.getClass())) {
            Map<String, String> siteAttributes = contentRepository.resolveSiteAttributes();
            ((SiteAttributesAwareTemplateEngine) argoTemplateEngine).setSiteAttributes(siteAttributes);
        }

        if (logger.isDebugEnabled()) logger.debug("Template engine: {}", argoTemplateEngine);

        ContentPage contentPage = prepareContent(pageInfo, processingMode);

        if (compilerInterceptors != null) {
            for (CompilerInterceptor compilerInterceptor : compilerInterceptors) {
                compilerInterceptor.afterPrepareContent(language, path, pageInfo, processingMode, contentPage);
            }
        }

        applyTemplate(pageInfo, contentPage, processingMode, template, templateVariation, argoTemplateEngine, outputStrategy);

        return templateVariation.getOutputContentType();
    }

    @Override
    public String compile(String path, PageInfo pageInfo, ContentPage preparedContentPage, ProcessingMode processingMode, CompilerOutputStrategy outputStrategy) {
        String pageName = pageInfo.getName();
        if (logger.isInfoEnabled()) logger.info("Compiling page '{}'.", pageName);

        TemplateReference templateReference = pageInfo.getTemplateReference();
        if (templateReference == null) {
            throw new IllegalStateException("No TemplateReference defined for PageInfo " + pageInfo);
        }
        if (logger.isDebugEnabled()) logger.debug("Template reference: {}", templateReference);

        String templateId = templateReference.getTemplateId();
        Template template = templateResolver.retrieve(project, templateId);
        if (template == null) {
            throw new IllegalStateException("No template found for id '" + templateId + "'!");
        }
        if (logger.isDebugEnabled()) logger.debug("Template: {}", templateId);

        String currentTemplateRevision = template.getRevision();
        String lastTemplateRevision = templateReference.getRevision();
        if (logger.isDebugEnabled())
            logger.debug("Last revision: {},\ncurrent revision: {}", lastTemplateRevision, currentTemplateRevision);

        Collection<TemplateVariation> variations = template.getVariations();
        if (variations == null || variations.isEmpty()) {
            throw new IllegalStateException("No template variations defined in template '" + templateId + "'!");
        }
        String templateVariationId = templateReference.getVariationId();
        if (logger.isDebugEnabled()) logger.debug("Template variation id: '{}'", templateVariationId);

        TemplateVariation templateVariation = null;
        for (TemplateVariation variation : variations) {
            if (templateVariationId.equals(variation.getId())) {
                templateVariation = variation;
                break;
            }
        }
        if (templateVariation == null) {
            throw new IllegalStateException("No template variation found in template '" + templateId + "' for id '" + templateVariationId + "'!");
        }
        if (logger.isDebugEnabled()) logger.debug("Template variation: '{}'", templateVariation);

        Type templateType = templateVariation.getType();
        if (logger.isDebugEnabled()) logger.debug("Template type: {}", templateType);

        ArgoTemplateEngine argoTemplateEngine = argoTemplateEngineFactory.getInstance(templateType);
        if (argoTemplateEngine == null) {
            throw new IllegalStateException("Unsupported template engine type '" + templateType + "'!");
        }

        // initialize the template engine
        argoTemplateEngine.setProject(project);
        argoTemplateEngine.setLanguage(language);
        argoTemplateEngine.setPath(path);

        if (PageInfoAwareTemplateEngine.class.isAssignableFrom(argoTemplateEngine.getClass())) {
            ((PageInfoAwareTemplateEngine) argoTemplateEngine).setPageInfo(pageInfo);
        }
        if (SiteAttributesAwareTemplateEngine.class.isAssignableFrom(argoTemplateEngine.getClass())) {
            Map<String, String> siteAttributes = contentRepository.resolveSiteAttributes();
            ((SiteAttributesAwareTemplateEngine) argoTemplateEngine).setSiteAttributes(siteAttributes);
        }

        if (logger.isDebugEnabled()) logger.debug("Template engine: {}", argoTemplateEngine);

        if (compilerInterceptors != null) {
            for (CompilerInterceptor compilerInterceptor : compilerInterceptors) {
                compilerInterceptor.afterPrepareContent(language, path, pageInfo, processingMode, preparedContentPage);
            }
        }

        applyTemplate(pageInfo, preparedContentPage, processingMode, template, templateVariation, argoTemplateEngine, outputStrategy);

        return templateVariation.getOutputContentType();
    }

    private void applyTemplate(
        SiteNodeInfo pageInfo,
        ContentPage preparedContentPage,
        ProcessingMode processingMode,
        Template template,
        TemplateVariation templateVariation,
        ArgoTemplateEngine argoTemplateEngine,
        CompilerOutputStrategy outputStrategy
    ) {
        // process content page
        String pageName = pageInfo.getName();

        String fileExtension = resolveFileExtension(templateVariation.getOutputContentType());
        try (OutputStream out = outputStrategy.getOutputStream(pageInfo, "", fileExtension, project, language)) {
            argoTemplateEngine.process(preparedContentPage, template, templateVariation, processingMode, out);
            if (logger.isInfoEnabled()) logger.info("Compilation of page '{}' done.", pageName);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) logger.error("Error compiling content!", ex);
            throw new IllegalStateException("Error occurred while compiling the page.", ex);
        }
    }

    private String resolveFileExtension(String outputContentType) {
        if (CONTENT_TYPE_EXTENSION_MAPPING.containsKey(outputContentType)) {
            return CONTENT_TYPE_EXTENSION_MAPPING.get(outputContentType);
        }

        throw new IllegalArgumentException("Unsupported output content type '" + outputContentType + "'!");
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setContentRepository(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public void setTemplateResolver(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    public void setArgoTemplateEngineFactory(ArgoTemplateEngineFactory argoTemplateEngineFactory) {
        this.argoTemplateEngineFactory = argoTemplateEngineFactory;
    }

    public void setDataSourcesResolver(DataSourcesResolver dataSourcesResolver) {
        this.dataSourcesResolver = dataSourcesResolver;
    }

    public void setContentPageProcessors(List<ContentPageProcessor> contentPageProcessors) {
        this.contentPageProcessors = contentPageProcessors;
    }

    public void setCompilerInterceptors(List<CompilerInterceptor> compilerInterceptors) {
        this.compilerInterceptors = compilerInterceptors;
    }

    public void setRuntimeConfigurationResolver(RuntimeConfigurationResolver runtimeConfigurationResolver) {
        this.runtimeConfigurationResolver = runtimeConfigurationResolver;
    }
}

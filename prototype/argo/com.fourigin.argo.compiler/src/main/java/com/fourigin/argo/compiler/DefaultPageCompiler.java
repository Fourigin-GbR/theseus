package com.fourigin.argo.compiler;

import com.fourigin.argo.compiler.datasource.DataSourcesResolver;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.models.template.TemplateReference;
import com.fourigin.argo.models.template.TemplateVariation;
import com.fourigin.argo.models.template.Type;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.TemplateResolver;
import com.fourigin.argo.strategies.CompilerOutputStrategy;
import com.fourigin.argo.template.engine.PageInfoAwareTemplateEngine;
import com.fourigin.argo.template.engine.ProcessingMode;
import com.fourigin.argo.template.engine.SiteAttributesAwareTemplateEngine;
import com.fourigin.argo.template.engine.TemplateEngine;
import com.fourigin.argo.template.engine.TemplateEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultPageCompiler implements PageCompiler {
    private String compilerBase;

    private ContentRepository contentRepository;

    private TemplateResolver templateResolver;

    private TemplateEngineFactory templateEngineFactory;

    private DataSourcesResolver dataSourcesResolver;

    private final Logger logger = LoggerFactory.getLogger(DefaultPageCompiler.class);

    static final Map<String, String> CONTENT_TYPE_EXTENSION_MAPPING = new HashMap<>();
    static {
        CONTENT_TYPE_EXTENSION_MAPPING.put("text/html", ".html");
    }

    public DefaultPageCompiler(String base) {
        this.compilerBase = base;
    }

    @Override
    public String getCompilerBase() {
        return compilerBase;
    }

    public ContentPage prepareContent(PageInfo pageInfo){
        String pageName = pageInfo.getName();
        if (logger.isInfoEnabled()) logger.info("Compiling page '{}'.", pageName);

        ContentPage contentPage = contentRepository.retrieve(pageInfo);
        if(contentPage == null){
            throw new IllegalStateException("No ContentPage assigned to PageInfo " + pageInfo);
        }
        if (logger.isDebugEnabled()) logger.debug("Content page: {}", contentPage.getId());

        // resolve all data sources
        if(dataSourcesResolver != null){
            if (logger.isDebugEnabled()) logger.debug("Resolving data sources of '{}'", pageName);
            contentPage = dataSourcesResolver.resolve(contentRepository, contentPage);
            if (logger.isDebugEnabled()) logger.debug("Content page with resolved data sources: {}", contentPage);
        }

        return contentPage;
    }

    @Override
    public String compile(PageInfo pageInfo, ProcessingMode processingMode, CompilerOutputStrategy outputStrategy) {
        String pageName = pageInfo.getName();
        if (logger.isInfoEnabled()) logger.info("Compiling page '{}'.", pageName);

        TemplateReference templateReference = pageInfo.getTemplateReference();
        if(templateReference == null){
            throw new IllegalStateException("No TemplateReference defined for PageInfo " + pageInfo);
        }
        if (logger.isDebugEnabled()) logger.debug("Template reference: {}", templateReference);

        String templateId = templateReference.getTemplateId();
        Template template = templateResolver.retrieve(templateId);
        if(template == null){
            throw new IllegalStateException("No template found for id '" + templateId + "'!");
        }
        if (logger.isDebugEnabled()) logger.debug("Template: {}", templateId);

        String currentTemplateRevision = template.getRevision();
        String lastTemplateRevision = templateReference.getRevision();
        if (logger.isDebugEnabled()) logger.debug("Last revision: {},\ncurrent revision: {}", lastTemplateRevision, currentTemplateRevision);

        Collection<TemplateVariation> variations = template.getVariations();
        if(variations == null || variations.isEmpty()){
            throw new IllegalStateException("No template variations defined in template '" + templateId + "'!");
        }
        String templateVariationId = templateReference.getVariationId();
        if (logger.isDebugEnabled()) logger.debug("Template variation id: '{}'", templateVariationId);

        TemplateVariation templateVariation = null;
        for (TemplateVariation variation : variations) {
            if(templateVariationId.equals(variation.getId())){
                templateVariation = variation;
                break;
            }
        }
        if(templateVariation == null){
            throw new IllegalStateException("No template variation found in template '" + templateId + "' for id '" + templateVariationId + "'!");
        }
        if (logger.isDebugEnabled()) logger.debug("Template variation: '{}'", templateVariation);

        Type templateType = templateVariation.getType();
        if (logger.isDebugEnabled()) logger.debug("Template type: {}", templateType);

        TemplateEngine templateEngine = templateEngineFactory.getInstance(templateType);
        if(templateEngine == null){
            throw new IllegalStateException("Unsupported template engine type '" + templateType + "'!");
        }

        // initialize the template engine
        templateEngine.setBase(compilerBase);

        if(PageInfoAwareTemplateEngine.class.isAssignableFrom(templateEngine.getClass())){
            ((PageInfoAwareTemplateEngine) templateEngine).setPageInfo(pageInfo);
        }
        if(SiteAttributesAwareTemplateEngine.class.isAssignableFrom(templateEngine.getClass())){
            Map<String, String> siteAttributes = contentRepository.resolveSiteAttributes();
            ((SiteAttributesAwareTemplateEngine) templateEngine).setSiteAttributes(siteAttributes);
        }

        if (logger.isDebugEnabled()) logger.debug("Template engine: {}", templateEngine);

        ContentPage contentPage = prepareContent(pageInfo);

        // process content page
        String fileExtension = resolveFileExtension(templateVariation.getOutputContentType());
        try (OutputStream out = outputStrategy.getOutputStream(pageInfo, "", fileExtension, compilerBase)) {
            templateEngine.process(contentPage, template, templateVariation, processingMode, out);
            if (logger.isInfoEnabled()) logger.info("Compilation of page '{}' done.", pageName);
        }
        catch(IOException ex){
            if (logger.isErrorEnabled()) logger.error("Error compiling content!", ex);
            throw new IllegalStateException("Error occurred while compiling the page.", ex);
        }

        return templateVariation.getOutputContentType();
    }

    private String resolveFileExtension(String outputContentType) {
        if(CONTENT_TYPE_EXTENSION_MAPPING.containsKey(outputContentType)){
            return CONTENT_TYPE_EXTENSION_MAPPING.get(outputContentType);
        }

        throw new IllegalArgumentException("Unsupported output content type '" + outputContentType + "'!");
    }

    public void setBase(String base) {
        this.compilerBase = base;
    }

    public void setContentRepository(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public void setTemplateResolver(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    public void setTemplateEngineFactory(TemplateEngineFactory templateEngineFactory) {
        this.templateEngineFactory = templateEngineFactory;
    }

    public void setDataSourcesResolver(DataSourcesResolver dataSourcesResolver) {
        this.dataSourcesResolver = dataSourcesResolver;
    }
}

package com.fourigin.cms.compiler;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.models.template.Template;
import com.fourigin.cms.models.template.TemplateReference;
import com.fourigin.cms.models.template.TemplateVariation;
import com.fourigin.cms.models.template.Type;
import com.fourigin.cms.repository.ContentRepository;
import com.fourigin.cms.template.engine.PageInfoAwareTemplateEngine;
import com.fourigin.cms.template.engine.ProcessingMode;
import com.fourigin.cms.template.engine.SiteAttributesAwareTemplateEngine;
import com.fourigin.cms.template.engine.TemplateEngine;
import com.fourigin.cms.template.engine.TemplateEngineFactory;
import com.fourigin.cms.repository.TemplateResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

public class DefaultPageCompiler implements PageCompiler {
    private String base;

    private ContentRepository contentRepository;

    private TemplateResolver templateResolver;

    private TemplateEngineFactory templateEngineFactory;

    private final Logger logger = LoggerFactory.getLogger(DefaultPageCompiler.class);

    @Override
    public String compile(PageInfo pageInfo, ProcessingMode processingMode, OutputStream out) {
        String pageName = pageInfo.getName();
        if (logger.isInfoEnabled()) logger.info("Compiling page '{}'.", pageName);

        TemplateReference templateReference = pageInfo.getTemplateReference();
        if(templateReference == null){
            throw new IllegalStateException("No TemplateReference defined for PageInfo " + pageInfo);
        }
        if (logger.isDebugEnabled()) logger.debug("Template reference: {}", templateReference);

        ContentPage contentPage = contentRepository.retrieve(pageInfo);
        if(contentPage == null){
            throw new IllegalStateException("No ContentPage assigned to PageInfo " + pageInfo);
        }
        if (logger.isDebugEnabled()) logger.debug("Content page: {}", contentPage.getId());

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
        templateEngine.setBase(base);

        if(PageInfoAwareTemplateEngine.class.isAssignableFrom(templateEngine.getClass())){
            ((PageInfoAwareTemplateEngine) templateEngine).setPageInfo(pageInfo);
        }
        if(SiteAttributesAwareTemplateEngine.class.isAssignableFrom(templateEngine.getClass())){
            Map<String, String> siteAttributes = contentRepository.resolveSiteAttributes();
            ((SiteAttributesAwareTemplateEngine) templateEngine).setSiteAttributes(siteAttributes);
        }

        if (logger.isDebugEnabled()) logger.debug("Template engine: {}", templateEngine);

        templateEngine.process(contentPage, template, templateVariation, processingMode, out);
        if (logger.isInfoEnabled()) logger.info("Compilation of page '{}' done.", pageName);

        return templateVariation.getOutputContentType();
    }

    public void setBase(String base) {
        this.base = base;
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
}

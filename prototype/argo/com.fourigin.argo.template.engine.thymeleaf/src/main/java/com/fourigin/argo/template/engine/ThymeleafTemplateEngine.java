package com.fourigin.argo.template.engine;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.models.template.TemplateVariation;
import com.fourigin.argo.template.engine.utilities.ContentElementUtilityFactory;
import com.fourigin.argo.template.engine.utilities.ContentPageAwareThymeleafTemplateUtility;
import com.fourigin.argo.template.engine.utilities.PageInfoAwareThymeleafTemplateUtility;
import com.fourigin.argo.template.engine.utilities.PagePropertiesUtilityFactory;
import com.fourigin.argo.template.engine.utilities.SiteAttributesAwareThymeleafTemplateUtility;
import com.fourigin.argo.template.engine.utilities.ThymeleafTemplateUtility;
import com.fourigin.argo.template.engine.utilities.ThymeleafTemplateUtilityFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ThymeleafTemplateEngine implements TemplateEngine, PageInfoAwareTemplateEngine, SiteAttributesAwareTemplateEngine {
    private org.thymeleaf.TemplateEngine thymeleafInternalTemplateEngine;

    private Map<String, ThymeleafTemplateUtilityFactory> templateUtilityFactories;

    private String utilitiesPrefix;

    private String base;

    private PageInfo pageInfo;

    private Map<String, String> siteAttributes;

    private static final String DEFAULT_UTILITIES_PREFIX = "util_";

    private static Map<String, ThymeleafTemplateUtilityFactory> standardTemplateUtilityFactories = new HashMap<>();

    static {
        standardTemplateUtilityFactories.put("content", new ContentElementUtilityFactory());
        standardTemplateUtilityFactories.put("page", new PagePropertiesUtilityFactory());
    }

    private final Logger logger = LoggerFactory.getLogger(ThymeleafTemplateEngine.class);

    public ThymeleafTemplateEngine duplicate(){
        ThymeleafTemplateEngine clone = new ThymeleafTemplateEngine();

        clone.setThymeleafInternalTemplateEngine(thymeleafInternalTemplateEngine);
        clone.setTemplateUtilityFactories(templateUtilityFactories);
        clone.setUtilitiesPrefix(utilitiesPrefix);

        return clone;
    }

    @Override
    public void process(ContentPage contentPage, Template template, TemplateVariation templateVariation, ProcessingMode processingMode, OutputStream out) {
        if(thymeleafInternalTemplateEngine == null){
            throw new IllegalStateException("No thymeleaf internal template engine defined!");
        }

        String templateName = template.getId() + "#" + templateVariation.getId();
        if (logger.isDebugEnabled()) logger.debug("Template name: {}.", templateName);

        Context context = new Context();
        context.setVariable(CONTENT_PAGE, contentPage);
        context.setVariable(PAGE_INFO, pageInfo);
        context.setVariable(SITE_ATTRIBUTES, siteAttributes);

        addUtilities(standardTemplateUtilityFactories, context, base, processingMode, false);

        addUtilities(templateUtilityFactories, context, base, processingMode, true);

        try(Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)){
            thymeleafInternalTemplateEngine.process(templateName, context, writer);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) logger.error("Error occurred while compiling content page!", ex);
            throw new ContentPageCompilerException("Error occurred while compiling content page!", ex);
        }
    }

    private void addUtilities(Map<String, ThymeleafTemplateUtilityFactory> utilities, Context context, String base, ProcessingMode processingMode, boolean usingPrefix){
        if(utilities != null && !utilities.isEmpty()){
            String prefix = utilitiesPrefix != null ? utilitiesPrefix : DEFAULT_UTILITIES_PREFIX;

            if (logger.isDebugEnabled()) logger.debug("Adding template utilities (using prefix '{}'):", prefix);
            for (Map.Entry<String, ThymeleafTemplateUtilityFactory> entry : utilities.entrySet()) {
                String utilityName = entry.getKey();
                ThymeleafTemplateUtilityFactory factory = entry.getValue();

                // initialize
                ThymeleafTemplateUtility utility = factory.getInstance();
                if(ContentPageAwareThymeleafTemplateUtility.class.isAssignableFrom(utility.getClass())){
                    ContentPage contentPage = (ContentPage) context.getVariable(CONTENT_PAGE);
                    ContentPageAwareThymeleafTemplateUtility contentPageUtility = ContentPageAwareThymeleafTemplateUtility.class.cast(utility);
                    contentPageUtility.setContentPage(contentPage);
                }
                if(PageInfoAwareThymeleafTemplateUtility.class.isAssignableFrom(utility.getClass())){
                    PageInfoAwareThymeleafTemplateUtility pageInfoUtility = PageInfoAwareThymeleafTemplateUtility.class.cast(utility);
                    pageInfoUtility.setPageInfo(pageInfo);
                }
                if(SiteAttributesAwareThymeleafTemplateUtility.class.isAssignableFrom(utility.getClass())){
                    SiteAttributesAwareThymeleafTemplateUtility siteAttributesUtility = SiteAttributesAwareThymeleafTemplateUtility.class.cast(utility);
                    siteAttributesUtility.setBase(base);
                    siteAttributesUtility.setSiteAttributes(siteAttributes);
                    siteAttributesUtility.setProcessingMode(processingMode);
                }

                // utility name
                String finalUtilityName;
                if(usingPrefix) {
                    finalUtilityName = prefix + utilityName;
                }
                else {
                    finalUtilityName = utilityName;
                }

                if (logger.isDebugEnabled()) logger.debug(" - {}, class {}", finalUtilityName, utility.getClass().getName());

                context.setVariable(finalUtilityName, utility);
            }
        }
    }

    public void setThymeleafInternalTemplateEngine(org.thymeleaf.TemplateEngine thymeleafInternalTemplateEngine) {
        this.thymeleafInternalTemplateEngine = thymeleafInternalTemplateEngine;
    }

    public void setTemplateUtilityFactories(Map<String, ThymeleafTemplateUtilityFactory> templateUtilityFactories) {
        this.templateUtilityFactories = templateUtilityFactories;
    }

    public void setUtilitiesPrefix(String utilitiesPrefix) {
        this.utilitiesPrefix = utilitiesPrefix;
    }

    @Override
    public void setBase(String base) {
        this.base = base;
    }

    @Override
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Override
    public void setSiteAttributes(Map<String, String> siteAttributes) {
        this.siteAttributes = siteAttributes;
    }
}

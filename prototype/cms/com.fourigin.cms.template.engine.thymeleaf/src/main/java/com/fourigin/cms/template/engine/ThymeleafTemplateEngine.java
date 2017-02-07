package com.fourigin.cms.template.engine;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.template.Template;
import com.fourigin.cms.models.template.TemplateVariation;
import javassist.ClassMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ThymeleafTemplateEngine implements TemplateEngine {
    private org.thymeleaf.TemplateEngine thymeleafInternalTemplateEngine;

    private Map<String, Object> templateUtilities;

    private String utilitiesPrefix;

    private static final String DEFAULT_UTILITIES_PREFIX = "util_";

    private static Map<String, Object> standardTemplateUtilities = new HashMap();

    static {
        standardTemplateUtilities.put("selector", new ContentElementSelector());
    }

    private final Logger logger = LoggerFactory.getLogger(ThymeleafTemplateEngine.class);

    @Override
    public void process(ContentPage contentPage, Template template, TemplateVariation templateVariation, OutputStream out) {
        if(thymeleafInternalTemplateEngine == null){
            throw new IllegalStateException("No thymeleaf internal template engine defined!");
        }

        String templateName = template.getId() + "#" + templateVariation.getId();
        if (logger.isDebugEnabled()) logger.debug("Template name: {}.", templateName);

        Context context = new Context();

        addUtilities(standardTemplateUtilities, context, false);

        addUtilities(templateUtilities, context, true);
        
        context.setVariable("content", contentPage);

        try(Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)){
            thymeleafInternalTemplateEngine.process(templateName, context, writer);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) logger.error("Error occurred while compiling content page!", ex);
            throw new IllegalStateException("Error occurred while compiling content page!", ex);
        }
    }

    private void addUtilities(Map<String, Object> utilities, Context context, boolean usingPrefix){
        if(utilities != null && !utilities.isEmpty()){
            String prefix = utilitiesPrefix != null ? utilitiesPrefix : DEFAULT_UTILITIES_PREFIX;

            if (logger.isDebugEnabled()) logger.debug("Adding template utilities (using prefix '{}'):", prefix);
            for (Map.Entry<String, Object> entry : utilities.entrySet()) {
                String utilityName = entry.getKey();
                Object utility = entry.getValue();

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

    public void setTemplateUtilities(Map<String, Object> templateUtilities) {
        this.templateUtilities = templateUtilities;
    }

    public void setUtilitiesPrefix(String utilitiesPrefix) {
        this.utilitiesPrefix = utilitiesPrefix;
    }
}

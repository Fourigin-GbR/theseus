package com.fourigin.cms.template.engine;

import com.fourigin.cms.models.template.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DefaultTemplateEngineFactory implements TemplateEngineFactory {
    private Map<Type, TemplateEngine> engines;

    private final Logger logger = LoggerFactory.getLogger(DefaultTemplateEngineFactory.class);

    @Override
    public TemplateEngine getInstance(Type type) {
        if(engines == null || engines.isEmpty()){
            if (logger.isErrorEnabled()) logger.error("No template engines configured!");
            return null;
        }

        if (logger.isInfoEnabled()) logger.info("Searching for template engine for type '{}'.", type);
        TemplateEngine result = engines.get(type);
        if(result == null){
            if (logger.isErrorEnabled()) logger.error("No template engine found for type '{}'!", type);
            return null;
        }

        return result.duplicate();
    }

    public void setEngines(Map<Type, TemplateEngine> engines) {
        this.engines = engines;
    }
}

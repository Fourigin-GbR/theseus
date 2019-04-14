package com.fourigin.argo.template.engine;

import com.fourigin.argo.models.template.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DefaultArgoTemplateEngineFactory implements ArgoTemplateEngineFactory {
    private Map<Type, ArgoTemplateEngine> engines;

    private final Logger logger = LoggerFactory.getLogger(DefaultArgoTemplateEngineFactory.class);

    @Override
    public ArgoTemplateEngine getInstance(Type type) {
        if (engines == null || engines.isEmpty()) {
            if (logger.isErrorEnabled()) logger.error("No template engines configured!");
            return null;
        }

        if (logger.isInfoEnabled()) logger.info("Searching for template engine for type '{}'.", type);
        ArgoTemplateEngine result = engines.get(type);
        if (result == null) {
            if (logger.isErrorEnabled()) logger.error("No template engine found for type '{}'!", type);
            return null;
        }

        return result.duplicate();
    }

    public void setEngines(Map<Type, ArgoTemplateEngine> engines) {
        this.engines = engines;
    }
}

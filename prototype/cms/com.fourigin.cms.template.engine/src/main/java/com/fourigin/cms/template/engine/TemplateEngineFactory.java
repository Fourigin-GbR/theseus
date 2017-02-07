package com.fourigin.cms.template.engine;

import com.fourigin.cms.models.template.Type;

public interface TemplateEngineFactory {
    TemplateEngine getInstance(Type type);
}

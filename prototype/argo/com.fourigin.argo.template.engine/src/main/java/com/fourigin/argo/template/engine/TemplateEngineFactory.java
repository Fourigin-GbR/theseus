package com.fourigin.argo.template.engine;

import com.fourigin.argo.models.template.Type;

public interface TemplateEngineFactory {
    TemplateEngine getInstance(Type type);
}

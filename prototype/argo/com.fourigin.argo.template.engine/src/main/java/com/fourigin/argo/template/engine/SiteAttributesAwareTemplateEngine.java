package com.fourigin.argo.template.engine;

import java.util.Map;

public interface SiteAttributesAwareTemplateEngine {
    void setSiteAttributes(Map<String, String> aiteAttributes);
}

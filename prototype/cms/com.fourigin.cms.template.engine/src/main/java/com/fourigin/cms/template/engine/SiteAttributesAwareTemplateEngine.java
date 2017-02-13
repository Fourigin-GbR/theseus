package com.fourigin.cms.template.engine;

import java.util.Map;

public interface SiteAttributesAwareTemplateEngine {
    void setSiteAttributes(Map<String, String> aiteAttributes);
}

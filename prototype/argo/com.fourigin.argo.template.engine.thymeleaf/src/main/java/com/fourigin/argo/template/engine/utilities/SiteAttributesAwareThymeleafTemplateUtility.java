package com.fourigin.argo.template.engine.utilities;

import java.util.Map;

public interface SiteAttributesAwareThymeleafTemplateUtility extends ThymeleafTemplateUtility {
    void setSiteAttributes(Map<String, String> siteAttributes);
}

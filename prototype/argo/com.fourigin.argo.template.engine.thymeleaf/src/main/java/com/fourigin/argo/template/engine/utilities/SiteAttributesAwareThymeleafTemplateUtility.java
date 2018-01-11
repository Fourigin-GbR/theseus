package com.fourigin.argo.template.engine.utilities;

import com.fourigin.argo.template.engine.ProcessingMode;

import java.util.Map;

public interface SiteAttributesAwareThymeleafTemplateUtility extends ThymeleafTemplateUtility {
    void setBase(String base);

    void setSiteAttributes(Map<String, String> siteAttributes);

    void setProcessingMode(ProcessingMode processingMode);
}

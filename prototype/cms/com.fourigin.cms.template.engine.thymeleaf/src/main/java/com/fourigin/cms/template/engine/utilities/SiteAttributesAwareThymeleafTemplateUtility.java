package com.fourigin.cms.template.engine.utilities;

import com.fourigin.cms.template.engine.ProcessingMode;

import java.util.Map;

public interface SiteAttributesAwareThymeleafTemplateUtility extends ThymeleafTemplateUtility {
    void setBase(String base);

    void setSiteAttributes(Map<String, String> siteAttributes);

    void setProcessingMode(ProcessingMode processingMode);
}

package com.fourigin.cms.template.engine.utilities;

public interface ThymeleafTemplateUtilityFactory<T extends ThymeleafTemplateUtility> {
    T getInstance();
}

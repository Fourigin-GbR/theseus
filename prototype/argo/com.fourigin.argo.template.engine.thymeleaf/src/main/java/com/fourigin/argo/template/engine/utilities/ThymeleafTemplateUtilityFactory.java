package com.fourigin.argo.template.engine.utilities;

public interface ThymeleafTemplateUtilityFactory<T extends ThymeleafTemplateUtility> {
    T getInstance();
}

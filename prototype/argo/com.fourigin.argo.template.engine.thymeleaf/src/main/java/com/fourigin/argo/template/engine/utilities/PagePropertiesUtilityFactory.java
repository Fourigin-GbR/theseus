package com.fourigin.argo.template.engine.utilities;

public class PagePropertiesUtilityFactory implements ThymeleafTemplateUtilityFactory<PagePropertiesUtility> {
    @Override
    public PagePropertiesUtility getInstance() {
        return new PagePropertiesUtility();
    }
}

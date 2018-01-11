package com.fourigin.argo.template.engine.utilities;

public class ContentElementUtilityFactory implements ThymeleafTemplateUtilityFactory<ContentElementUtility> {
    @Override
    public ContentElementUtility getInstance() {
        return new ContentElementUtility();
    }
}

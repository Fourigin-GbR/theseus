package com.fourigin.cms.template.engine.utilities;

public class ContentElementUtilityFactory implements ThymeleafTemplateUtilityFactory<ContentElementUtility> {
    @Override
    public ContentElementUtility getInstance() {
        return new ContentElementUtility();
    }
}

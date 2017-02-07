package com.fourigin.cms.models.content.elements.mapping;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fourigin.cms.models.content.elements.ContentElement;

public class ContentPageModule extends SimpleModule {
    public ContentPageModule() {
        super("ContentPage", Version.unknownVersion());
        setMixInAnnotation(ContentElement.class, ContentElementMixin.class);
    }
}
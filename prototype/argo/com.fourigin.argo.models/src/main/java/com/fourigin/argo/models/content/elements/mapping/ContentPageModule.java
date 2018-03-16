package com.fourigin.argo.models.content.elements.mapping;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.ContentListElement;

public class ContentPageModule extends SimpleModule {
    private static final long serialVersionUID = 7040244261823663045L;

    public ContentPageModule() {
        super("ContentPage", Version.unknownVersion());

        setMixInAnnotation(ContentElement.class, ContentElementMixin.class);
        setMixInAnnotation(ContentListElement.class, ContentListElementMixin.class);
    }
}

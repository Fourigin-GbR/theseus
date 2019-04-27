package com.fourigin.argo.models.content.elements.mapping;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.ListElement;
import com.fourigin.argo.models.structure.ContentPageChecksum;

public class ContentPageModule extends SimpleModule {
    private static final long serialVersionUID = 7040244261823663045L;
    
    public ContentPageModule() {
        super("ContentPage", Version.unknownVersion());

        setMixInAnnotation(ContentElement.class, ContentElementMixin.class);
        setMixInAnnotation(ListElement.class, ContentListElementMixin.class);
        setMixInAnnotation(ContentPageChecksum.class, ContentPageChecksumMixin.class);
    }
}

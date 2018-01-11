package com.fourigin.theseus.web.mapping;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fourigin.theseus.core.Translation;

public class TranslationModule extends SimpleModule {
    private static final long serialVersionUID = 7040244261823663045L;

    public TranslationModule() {
        super("Translation", Version.unknownVersion());
        setMixInAnnotation(Translation.class, TranslationMixin.class);
    }
}
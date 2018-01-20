package com.fourigin.theseus.web.mapping;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fourigin.theseus.core.Price;

public class PriceModule extends SimpleModule {
    private static final long serialVersionUID = 7040244261823663045L;

    public PriceModule() {
        super("Price", Version.unknownVersion());
        setMixInAnnotation(Price.class, PriceMixin.class);
    }
}

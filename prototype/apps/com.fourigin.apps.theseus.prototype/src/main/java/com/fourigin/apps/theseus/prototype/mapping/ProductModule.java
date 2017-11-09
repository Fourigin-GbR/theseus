package com.fourigin.apps.theseus.prototype.mapping;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fourigin.theseus.core.Product;
import com.fourigin.theseus.core.types.PropertyType;

public class ProductModule extends SimpleModule {
    private static final long serialVersionUID = 7040244261823663045L;

    public ProductModule() {
        super("Product", Version.unknownVersion());
        setMixInAnnotation(Product.class, ProductMixin.class);
        setMixInAnnotation(PropertyType.class, PropertyTypeMixin.class);
    }
}
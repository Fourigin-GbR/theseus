package com.fourigin.theseus.web.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings({
    "PMD.AbstractClassWithoutAnyMethod",
    "PMD.AbstractClassWithoutAbstractMethod"
})
public abstract class PriceMixin {
    @JsonIgnore
    abstract double getPrice();
}

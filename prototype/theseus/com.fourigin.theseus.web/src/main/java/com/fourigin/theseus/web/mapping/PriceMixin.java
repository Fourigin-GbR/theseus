package com.fourigin.theseus.web.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings({
    "PMD.AbstractClassWithoutAnyMethod",
    "PMD.AbstractClassWithoutAbstractMethod"
})
abstract public class PriceMixin {
    @JsonIgnore
    abstract double getPrice();
}

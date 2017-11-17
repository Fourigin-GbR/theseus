package com.fourigin.apps.theseus.prototype.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;

abstract public class PriceMixin {
    @JsonIgnore
    abstract double getPrice();
}

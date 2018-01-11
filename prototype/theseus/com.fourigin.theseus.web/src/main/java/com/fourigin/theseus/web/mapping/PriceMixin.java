package com.fourigin.theseus.web.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;

abstract public class PriceMixin {
    @JsonIgnore
    abstract double getPrice();
}

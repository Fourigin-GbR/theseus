package com.fourigin.theseus.web.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

@SuppressWarnings({
    "PMD.AbstractClassWithoutAnyMethod",
    "PMD.AbstractClassWithoutAbstractMethod"
})
abstract public class ProductMixin {
    @JsonProperty("name")
    @JsonDeserialize(contentAs = Map.class)
    abstract Map<String, String> getName();
}

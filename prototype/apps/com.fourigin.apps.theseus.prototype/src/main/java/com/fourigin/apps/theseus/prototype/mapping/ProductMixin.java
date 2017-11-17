package com.fourigin.apps.theseus.prototype.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

abstract public class ProductMixin {
    @JsonProperty("name")
    @JsonDeserialize(contentAs = Map.class)
    abstract Map<String, String> getName();
}

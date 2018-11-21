package com.fourigin.argo.forms.normalizer.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fourigin.argo.forms.normalizer.RegexDataNormalizer;
import com.fourigin.argo.forms.normalizer.TrimDataNormalizer;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = RegexDataNormalizer.class, name = "regex"),
    @JsonSubTypes.Type(value = TrimDataNormalizer.class, name = "trim"),
})
@SuppressWarnings({
    "PMD.AbstractClassWithoutAnyMethod",
    "PMD.AbstractClassWithoutAbstractMethod"
})
public abstract class DataNormalizerMixin {
    @JsonIgnore
    abstract String getDisplayName();
}

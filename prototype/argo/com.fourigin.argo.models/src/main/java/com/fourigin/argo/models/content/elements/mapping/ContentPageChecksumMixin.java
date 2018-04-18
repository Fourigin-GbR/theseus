package com.fourigin.argo.models.content.elements.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings({
    "PMD.AbstractClassWithoutAnyMethod",
    "PMD.AbstractClassWithoutAbstractMethod"
})
public abstract class ContentPageChecksumMixin {
    @JsonIgnore abstract String getCombinedValue();
}

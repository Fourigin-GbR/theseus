package com.fourigin.argo.repository.model.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fourigin.argo.repository.model.JsonDirectoryInfo;
import com.fourigin.argo.repository.model.JsonFileInfo;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = JsonFileInfo.class, name = "file"),
  @JsonSubTypes.Type(value = JsonDirectoryInfo.class, name = "dir")
})
@SuppressWarnings({
    "PMD.AbstractClassWithoutAnyMethod",
    "PMD.AbstractClassWithoutAbstractMethod"
})
public abstract class JsonInfoMixin {
}

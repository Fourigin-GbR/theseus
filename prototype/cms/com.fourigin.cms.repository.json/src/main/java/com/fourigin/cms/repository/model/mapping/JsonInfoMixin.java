package com.fourigin.cms.repository.model.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fourigin.cms.repository.model.JsonDirectoryInfo;
import com.fourigin.cms.repository.model.JsonFileInfo;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = JsonFileInfo.class, name = "file"),
  @JsonSubTypes.Type(value = JsonDirectoryInfo.class, name = "dir")
})
abstract public class JsonInfoMixin {
}

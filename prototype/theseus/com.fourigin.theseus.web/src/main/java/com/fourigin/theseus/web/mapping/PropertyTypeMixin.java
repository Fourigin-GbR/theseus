package com.fourigin.theseus.web.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fourigin.theseus.core.types.EditablePropertyType;
import com.fourigin.theseus.core.types.NoValuePropertyType;
import com.fourigin.theseus.core.types.SetPropertyType;

import java.util.Map;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "withType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = NoValuePropertyType.class, name = "available"),
    @JsonSubTypes.Type(value = EditablePropertyType.class, name = "editable"),
    @JsonSubTypes.Type(value = SetPropertyType.class, name = "predefined")
})
@SuppressWarnings({
    "PMD.AbstractClassWithoutAnyMethod",
    "PMD.AbstractClassWithoutAbstractMethod"
})
public abstract class PropertyTypeMixin {
    @JsonProperty("name")
    @JsonDeserialize(contentAs = Map.class)
    abstract Map<String, String> getName();
}

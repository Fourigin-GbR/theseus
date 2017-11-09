package com.fourigin.apps.theseus.prototype.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

//@JsonTypeInfo(
//  use = JsonTypeInfo.Id.NAME,
//  include = JsonTypeInfo.As.PROPERTY,
//  property = "type")
//@JsonSubTypes({
//    @JsonSubTypes.Type(value = ContentGroup.class, name = "group"),
//    @JsonSubTypes.Type(value = TextContentElement.class, name = "text"),
//    @JsonSubTypes.Type(value = TextLinkContentElement.class, name = "text-link"),
//    @JsonSubTypes.Type(value = ObjectContentElement.class, name = "object"),
//    @JsonSubTypes.Type(value = ObjectLinkContentElement.class, name = "object-link"),
//    @JsonSubTypes.Type(value = ContentList.class, name = "list")
//})
abstract public class ProductMixin {
    @JsonProperty("name")
    @JsonDeserialize(contentAs = Map.class)
    abstract Map<String, String> getName();

}

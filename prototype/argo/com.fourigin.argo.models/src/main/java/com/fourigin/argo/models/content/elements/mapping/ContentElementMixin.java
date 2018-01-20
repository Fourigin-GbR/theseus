package com.fourigin.argo.models.content.elements.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fourigin.argo.models.content.elements.ContentGroup;
import com.fourigin.argo.models.content.elements.ContentList;
import com.fourigin.argo.models.content.elements.ObjectContentElement;
import com.fourigin.argo.models.content.elements.ObjectLinkContentElement;
import com.fourigin.argo.models.content.elements.TextContentElement;
import com.fourigin.argo.models.content.elements.TextLinkContentElement;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ContentGroup.class, name = "group"),
    @JsonSubTypes.Type(value = TextContentElement.class, name = "text"),
    @JsonSubTypes.Type(value = TextLinkContentElement.class, name = "text-link"),
    @JsonSubTypes.Type(value = ObjectContentElement.class, name = "object"),
    @JsonSubTypes.Type(value = ObjectLinkContentElement.class, name = "object-link"),
    @JsonSubTypes.Type(value = ContentList.class, name = "list")
})
@SuppressWarnings({
    "PMD.AbstractClassWithoutAnyMethod",
    "PMD.AbstractClassWithoutAbstractMethod"
})
public abstract class ContentElementMixin {
}

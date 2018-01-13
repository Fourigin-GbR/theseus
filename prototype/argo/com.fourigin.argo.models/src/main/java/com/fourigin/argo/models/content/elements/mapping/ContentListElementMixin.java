package com.fourigin.argo.models.content.elements.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fourigin.argo.models.content.elements.list.GroupContentListElement;
import com.fourigin.argo.models.content.elements.list.ObjectContentListElement;
import com.fourigin.argo.models.content.elements.list.ObjectLinkContentListElement;
import com.fourigin.argo.models.content.elements.list.TextContentListElement;
import com.fourigin.argo.models.content.elements.list.TextLinkContentListElement;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TextContentListElement.class, name = "list-text"),
    @JsonSubTypes.Type(value = TextLinkContentListElement.class, name = "list-text-link"),
    @JsonSubTypes.Type(value = ObjectContentListElement.class, name = "list-object"),
    @JsonSubTypes.Type(value = ObjectLinkContentListElement.class, name = "list-object-link"),
    @JsonSubTypes.Type(value = GroupContentListElement.class, name = "list-group")
})
@SuppressWarnings({
    "PMD.AbstractClassWithoutAnyMethod",
    "PMD.AbstractClassWithoutAbstractMethod"
})
abstract public class ContentListElementMixin {
}

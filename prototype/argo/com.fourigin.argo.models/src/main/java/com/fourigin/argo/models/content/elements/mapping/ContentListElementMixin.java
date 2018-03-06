package com.fourigin.argo.models.content.elements.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fourigin.argo.models.content.elements.list.GroupContentListElement;
import com.fourigin.argo.models.content.elements.list.LinkListElement;
import com.fourigin.argo.models.content.elements.list.ObjectContentListElement;
import com.fourigin.argo.models.content.elements.list.TextContentListElement;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TextContentListElement.class, name = "list-text"),
    @JsonSubTypes.Type(value = ObjectContentListElement.class, name = "list-object"),
    @JsonSubTypes.Type(value = LinkListElement.class, name = "list-link"),
    @JsonSubTypes.Type(value = GroupContentListElement.class, name = "list-group")
})
@SuppressWarnings({
    "PMD.AbstractClassWithoutAnyMethod",
    "PMD.AbstractClassWithoutAbstractMethod"
})
public abstract class ContentListElementMixin {
}

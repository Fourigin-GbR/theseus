package com.fourigin.argo.models.content.elements.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fourigin.argo.models.content.elements.ContentSubList;
import com.fourigin.argo.models.content.elements.DataContentListElement;
import com.fourigin.argo.models.content.elements.GroupContentListElement;
import com.fourigin.argo.models.content.elements.LinkListElement;
import com.fourigin.argo.models.content.elements.ObjectContentListElement;
import com.fourigin.argo.models.content.elements.TextContentListElement;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TextContentListElement.class, name = "list-text"),
    @JsonSubTypes.Type(value = ObjectContentListElement.class, name = "list-object"),
    @JsonSubTypes.Type(value = LinkListElement.class, name = "list-link"),
    @JsonSubTypes.Type(value = GroupContentListElement.class, name = "list-group"),
    @JsonSubTypes.Type(value = DataContentListElement.class, name = "list-data"),
    @JsonSubTypes.Type(value = ContentSubList.class, name = "sub-list"),
})
@SuppressWarnings({
    "PMD.AbstractClassWithoutAnyMethod",
    "PMD.AbstractClassWithoutAbstractMethod"
})
public abstract class ContentListElementMixin {
}

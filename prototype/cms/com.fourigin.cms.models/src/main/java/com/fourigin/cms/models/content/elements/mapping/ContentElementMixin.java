package com.fourigin.cms.models.content.elements.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fourigin.cms.models.content.elements.ContentGroup;
import com.fourigin.cms.models.content.elements.ObjectContentElement;
import com.fourigin.cms.models.content.elements.ObjectLinkContentElement;
import com.fourigin.cms.models.content.elements.TextContentElement;
import com.fourigin.cms.models.content.elements.TextLinkContentElement;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = ContentGroup.class, name = "group"),
  @JsonSubTypes.Type(value = TextContentElement.class, name = "text"),
  @JsonSubTypes.Type(value = TextLinkContentElement.class, name = "text-link"),
  @JsonSubTypes.Type(value = ObjectContentElement.class, name = "object"),
  @JsonSubTypes.Type(value = ObjectLinkContentElement.class, name = "object-link")
})
abstract public class ContentElementMixin {
}

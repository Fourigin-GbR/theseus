package com.fourigin.cms.models.content.elements;

public interface TextAwareContentElement {

    String getContent();
    void setContent(String content);

    boolean isMarkupAllowed();
    void setMarkupAllowed(boolean markupAllowed);
}

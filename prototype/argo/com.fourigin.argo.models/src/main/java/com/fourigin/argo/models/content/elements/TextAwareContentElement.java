package com.fourigin.argo.models.content.elements;

public interface TextAwareContentElement {

    String getContent();
    void setContent(String content);

    String getContextSpecificContent(String context, boolean fallback);
    void setContextSpecificContent(String context, String content);
}

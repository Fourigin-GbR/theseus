package com.fourigin.argo.models.content.elements;

public interface ObjectAwareContentElement {

    String getReferenceId();
    void setReferenceId(String referenceId);

    String getSource();
    void setSource(String source);

    String getAlternateText();
    void setAlternateText(String alternateText);

    String getMimeType();
    void setMimeType(String mimeType);

}

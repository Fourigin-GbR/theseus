package com.fourigin.cms.models.content.elements;

public class ObjectContentElement extends AbstractContentElement implements ContentElement {
    private String referenceId;
    private String source;
    private String alternateText;
    private String mimeType;

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAlternateText() {
        return alternateText;
    }

    public void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
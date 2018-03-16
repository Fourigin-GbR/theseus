package com.fourigin.argo.models.content.elements;

import java.util.Objects;

public class ObjectContentListElement extends AbstractContentListElement implements ObjectAwareContentElement, ContentListElement {

    private static final long serialVersionUID = 3964141627291956922L;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectContentListElement)) return false;
        if (!super.equals(o)) return false;
        ObjectContentListElement that = (ObjectContentListElement) o;
        return Objects.equals(referenceId, that.referenceId) &&
            Objects.equals(source, that.source) &&
            Objects.equals(alternateText, that.alternateText) &&
            Objects.equals(mimeType, that.mimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), referenceId, source, alternateText, mimeType);
    }

    @Override
    public String toString() {
        return "ObjectContentListElement{" +
            "referenceId='" + referenceId + '\'' +
            ", source='" + source + '\'' +
            ", alternateText='" + alternateText + '\'' +
            ", mimeType='" + mimeType + '\'' +
            '}';
    }

    public static class Builder {
        private String title;
        private String referenceId;
        private String source;
        private String alternateText;
        private String mimeType;

        public Builder withTitle(String title){
            this.title = title;
            return this;
        }

        public Builder withReferenceId(String referenceId){
            this.referenceId = referenceId;
            return this;
        }

        public Builder withSource(String source){
            this.source = source;
            return this;
        }

        public Builder withAlternateText(String alternateText){
            this.alternateText = alternateText;
            return this;
        }

        public Builder withMimeType(String mimeType){
            this.mimeType = mimeType;
            return this;
        }

        public ObjectContentListElement build(){
            ObjectContentListElement element = new ObjectContentListElement();
            element.setTitle(title);
            element.setReferenceId(referenceId);
            element.setSource(source);
            element.setAlternateText(alternateText);
            element.setMimeType(mimeType);
            return element;
        }
    }
}

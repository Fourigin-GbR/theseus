package com.fourigin.argo.models.content.elements.list;

import com.fourigin.argo.models.content.elements.ObjectAwareContentElement;

public class ObjectContentListElement implements ObjectAwareContentElement, ContentListElement {

    private static final long serialVersionUID = 3964141627291956922L;

    private String title;
    private String referenceId;
    private String source;
    private String alternateText;
    private String mimeType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

        ObjectContentListElement that = (ObjectContentListElement) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (referenceId != null ? !referenceId.equals(that.referenceId) : that.referenceId != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        //noinspection SimplifiableIfStatement
        if (alternateText != null ? !alternateText.equals(that.alternateText) : that.alternateText != null)
            return false;
        return mimeType != null ? mimeType.equals(that.mimeType) : that.mimeType == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (referenceId != null ? referenceId.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (alternateText != null ? alternateText.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ObjectContentListElement{" +
            "title='" + title + '\'' +
            ", referenceId='" + referenceId + '\'' +
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

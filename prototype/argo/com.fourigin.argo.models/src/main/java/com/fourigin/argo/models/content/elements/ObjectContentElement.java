package com.fourigin.argo.models.content.elements;

import java.util.HashMap;
import java.util.Map;

public class ObjectContentElement extends AbstractContentElement implements ObjectAwareContentElement, ContentElement {
    private static final long serialVersionUID = -8071563579520012186L;

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
        if (!(o instanceof ObjectContentElement)) return false;
        if (!super.equals(o)) return false;

        ObjectContentElement that = (ObjectContentElement) o;

        if (referenceId != null ? !referenceId.equals(that.referenceId) : that.referenceId != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        //noinspection SimplifiableIfStatement
        if (alternateText != null ? !alternateText.equals(that.alternateText) : that.alternateText != null)
            return false;
        return mimeType != null ? mimeType.equals(that.mimeType) : that.mimeType == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (referenceId != null ? referenceId.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (alternateText != null ? alternateText.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ObjectContentElement{" +
          "name='" + getName() + '\'' +
          ", title='" + getTitle() + '\'' +
          ", referenceId='" + referenceId + '\'' +
          ", source='" + source + '\'' +
          ", alternateText='" + alternateText + '\'' +
          ", mimeType='" + mimeType + '\'' +
          '}';
    }

    public static class Builder {
        private String name;
        private String title;
        private String referenceId;
        private String source;
        private String alternateText;
        private String mimeType;
        private Map<String, String> attributes = new HashMap<>();

        public Builder withName(String name){
            this.name = name;
            return this;
        }

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

        public Builder withAttribute(String key, String value){
            if(key != null) {
                if (value == null) {
                    this.attributes.remove(key);
                } else {
                    this.attributes.put(key, value);
                }
            }

            return this;
        }


        public ObjectContentElement build(){
            ObjectContentElement element = new ObjectContentElement();
            element.setName(name);
            element.setTitle(title);
            element.setReferenceId(referenceId);
            element.setSource(source);
            element.setAlternateText(alternateText);
            element.setMimeType(mimeType);
            if(!attributes.isEmpty()){
                element.setAttributes(attributes);
            }
            return element;
        }
    }
}
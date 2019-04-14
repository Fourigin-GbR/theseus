package com.fourigin.argo.models.content.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ObjectContentElement extends AbstractContentElement implements ObjectAwareContentElement, ContentElement {
    private static final long serialVersionUID = -8071563579520012186L;

    private LanguageContent title;
    private String referenceId;
    private String source;
    private String alternateText;
    private String mimeType;

    @Override
    public LanguageContent getTitle() {
        return title;
    }

    @Override
    public void setTitle(LanguageContent title) {
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
        if (!(o instanceof ObjectContentElement)) return false;
        if (!super.equals(o)) return false;
        ObjectContentElement that = (ObjectContentElement) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(referenceId, that.referenceId) &&
            Objects.equals(source, that.source) &&
            Objects.equals(alternateText, that.alternateText) &&
            Objects.equals(mimeType, that.mimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, referenceId, source, alternateText, mimeType);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ObjectContentElement{");

        builder.append("name='").append(getName()).append('\'');

        LanguageContent title = getTitle();
        if(title != null) {
            builder.append(", title='").append(title).append('\'');
        }

        builder.append(", referenceId='").append(referenceId).append('\'');
        builder.append(", source='").append(source).append('\'');
        builder.append(", alternateText='").append(alternateText).append('\'');
        builder.append(", mimeType='").append(mimeType).append('\'');

        Map<String, String> attributes = getAttributes();
        if (attributes != null) {
            builder.append(", attributes='").append(attributes).append('\'');
        }

        builder.append('}');

        return builder.toString();
    }

    public static class Builder {
        private String name;
        private LanguageContent title = new LanguageContent();
        private String referenceId;
        private String source;
        private String alternateText;
        private String mimeType;
        private Map<String, String> attributes = new HashMap<>();

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withTitle(String language, String title){
            this.title.put(language, title);
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
            if (!title.isEmpty()) {
                element.setTitle(title);
            }
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

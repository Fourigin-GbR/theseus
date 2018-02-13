package com.fourigin.argo.models.content.elements;

import java.util.HashMap;
import java.util.Map;

public class ObjectLinkContentElement extends AbstractContentElement implements ObjectAwareContentElement, LinkAwareContentElement, ContentElement {
    private static final long serialVersionUID = -400773903221846863L;

    private String referenceId;
    private String source;
    private String alternateText;
    private String mimeType;
    private String url;
    private String anchorName;
    private String target;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectLinkContentElement)) return false;
        if (!super.equals(o)) return false;

        ObjectLinkContentElement that = (ObjectLinkContentElement) o;

        if (referenceId != null ? !referenceId.equals(that.referenceId) : that.referenceId != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (alternateText != null ? !alternateText.equals(that.alternateText) : that.alternateText != null)
            return false;
        if (mimeType != null ? !mimeType.equals(that.mimeType) : that.mimeType != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        //noinspection SimplifiableIfStatement
        if (anchorName != null ? !anchorName.equals(that.anchorName) : that.anchorName != null) return false;
        return target != null ? target.equals(that.target) : that.target == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (referenceId != null ? referenceId.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (alternateText != null ? alternateText.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (anchorName != null ? anchorName.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ObjectLinkContentElement{" +
          "name='" + getName() + '\'' +
          ", title='" + getTitle() + '\'' +
          ", referenceId='" + referenceId + '\'' +
          ", source='" + source + '\'' +
          ", alternateText='" + alternateText + '\'' +
          ", mimeType='" + mimeType + '\'' +
          ", url='" + url + '\'' +
          ", anchorName='" + anchorName + '\'' +
          ", target='" + target + '\'' +
          '}';
    }

    public static class Builder {
        private String name;
        private String title;
        private String referenceId;
        private String source;
        private String alternateText;
        private String mimeType;
        private String url;
        private String anchorName;
        private String target;
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

        public Builder withUrl(String url){
            this.url = url;
            return this;
        }

        public Builder withAnchorName(String anchorName){
            this.anchorName = anchorName;
            return this;
        }

        public Builder withTarget(String target){
            this.target = target;
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

        public ObjectLinkContentElement build(){
            ObjectLinkContentElement element = new ObjectLinkContentElement();
            element.setName(name);
            element.setTitle(title);
            element.setReferenceId(referenceId);
            element.setSource(source);
            element.setAlternateText(alternateText);
            element.setMimeType(mimeType);
            element.setUrl(url);
            element.setAnchorName(anchorName);
            element.setTarget(target);
            if(!attributes.isEmpty()){
                element.setAttributes(attributes);
            }
            return element;
        }
    }
}
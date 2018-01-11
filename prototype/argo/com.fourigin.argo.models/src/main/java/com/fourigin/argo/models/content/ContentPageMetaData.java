package com.fourigin.argo.models.content;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ContentPageMetaData implements Serializable {
    private static final long serialVersionUID = 6682484501791045240L;

    private String title;
    private Map<String, String> attributes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "ContentPageMetaData{" +
          "title='" + title + '\'' +
          ", attributes=" + attributes +
          '}';
    }

    public static class Builder {
        private String title;
        private Map<String, String> attributes;

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder attributes(Map<String, String> attributes){
            this.attributes = attributes;
            return this;
        }

        public Builder attribute(String name, String value){
            if(name == null){
                throw new IllegalArgumentException("Attribute name must not be null!");
            }

            if(attributes == null){
                attributes = new HashMap<>();
            }

            if(value == null){
                attributes.remove(name);
            }
            else {
                attributes.put(name, value);
            }

            return this;
        }

        public ContentPageMetaData build(){
            ContentPageMetaData metaData = new ContentPageMetaData();
            metaData.setTitle(title);
            metaData.setAttributes(attributes);
            return metaData;
        }
    }
}

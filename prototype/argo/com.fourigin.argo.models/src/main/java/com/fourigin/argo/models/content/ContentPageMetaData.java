package com.fourigin.argo.models.content;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ContentPageMetaData implements Serializable {
    private static final long serialVersionUID = 6682484501791045240L;

    private String title;
    private Map<String, String> contextSpecificTitles;
    private Map<String, String> attributes;

    public ContentPageMetaData() {
    }

    public ContentPageMetaData(ContentPageMetaData prototype) {
        this.title = prototype.title;
        this.attributes = new HashMap<>(prototype.attributes);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, String> getContextSpecificTitles() {
        return contextSpecificTitles;
    }

    public void setContextSpecificTitles(Map<String, String> contextSpecificTitles) {
        this.contextSpecificTitles = contextSpecificTitles;
    }

    public String getContextSpecificTitle(String context, boolean fallback) {
        if (contextSpecificTitles == null || contextSpecificTitles.isEmpty()) {
            return fallback ? title : null;
        }

        if (!contextSpecificTitles.containsKey(context)) {
            return fallback ? title : null;
        }

        return contextSpecificTitles.get(context);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentPageMetaData)) return false;
        ContentPageMetaData that = (ContentPageMetaData) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(contextSpecificTitles, that.contextSpecificTitles) &&
            Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, contextSpecificTitles, attributes);
    }

    @Override
    public String toString() {
        return "ContentPageMetaData{" +
            "title='" + title + '\'' +
            ", contextSpecificTitles=" + contextSpecificTitles +
            ", attributes=" + attributes +
            '}';
    }

    public static class Builder {
        private String title;
        private Map<String, String> contextSpecificTitles;
        private Map<String, String> attributes;

        public Builder withTitle(String title){
            this.title = title;
            return this;
        }

        public Builder withContextSpecificTitles(Map<String, String> attributes){
            this.attributes = attributes;
            return this;
        }

        public Builder withContextSpecificTitle(String name, String value){
            if(name == null){
                throw new IllegalArgumentException("Title context must not be null!");
            }

            if(contextSpecificTitles == null){
                contextSpecificTitles = new HashMap<>();
            }

            if(value == null){
                contextSpecificTitles.remove(name);
            }
            else {
                contextSpecificTitles.put(name, value);
            }

            return this;
        }

        public Builder withAttributes(Map<String, String> attributes){
            this.attributes = attributes;
            return this;
        }

        public Builder withAttribute(String name, String value){
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
            metaData.setContextSpecificTitles(contextSpecificTitles);
            metaData.setAttributes(attributes);
            return metaData;
        }
    }
}

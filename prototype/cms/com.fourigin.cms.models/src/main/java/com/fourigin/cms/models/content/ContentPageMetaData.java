package com.fourigin.cms.models.content;

import java.util.Map;

public class ContentPageMetaData {
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
}

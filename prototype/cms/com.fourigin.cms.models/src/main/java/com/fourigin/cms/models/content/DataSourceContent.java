package com.fourigin.cms.models.content;

import com.fourigin.cms.models.content.elements.ContentElement;

import java.util.List;
import java.util.Map;

public class DataSourceContent {
    private String type;
    private Map<String, Object> query;
    private String checksum;
    private List<ContentElement> content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public List<ContentElement> getContent() {
        return content;
    }

    public void setContent(List<ContentElement> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "DataSourceContent{" +
          "type='" + type + '\'' +
          ", query=" + query +
          ", checksum='" + checksum + '\'' +
          ", content=" + content +
          '}';
    }
}
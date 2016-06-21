package com.fourigin.cms.models.content;

import com.fourigin.cms.models.content.elements.ContentElement;
import com.fourigin.cms.models.datasource.DataSourceIdentifier;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DataSourceContent implements Serializable {
    private static final long serialVersionUID = 5480644248235587413L;

    private DataSourceIdentifier identifier;
    private List<ContentElement> content;

    public DataSourceIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(DataSourceIdentifier identifier) {
        this.identifier = identifier;
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
          "identifier=" + identifier +
          ", content=" + content +
          '}';
    }
}
package com.fourigin.cms.models.content;

import com.fourigin.cms.models.content.elements.ContentElement;

import java.util.List;
import java.util.Map;

public interface DataSourceContent {
    String getType();

    Map<String, Object> getQuery();

    String getChecksum();

    ContentElement getContent();

    void setContent(ContentElement content);
}
package com.fourigin.cms.compiler.datasource;

import com.fourigin.cms.models.content.elements.ContentElement;

public interface DataSourceResponse {
    String getChecksum();
    ContentElement getContent();
}

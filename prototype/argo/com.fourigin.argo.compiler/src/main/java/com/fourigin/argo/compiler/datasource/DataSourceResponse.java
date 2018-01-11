package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.content.elements.ContentElement;

public interface DataSourceResponse {
    String getChecksum();
    ContentElement getContent();
}

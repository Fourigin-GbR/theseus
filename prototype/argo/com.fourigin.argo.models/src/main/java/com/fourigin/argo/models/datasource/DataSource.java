package com.fourigin.argo.models.datasource;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.structure.nodes.PageInfo;

import java.util.Map;

public interface DataSource<T extends DataSourceQuery> {
    String getType();
    ContentElement generateContent(PageInfo ownerPage, DataSourceIdentifier id, T query, Map<String, Object> context);
}

package com.fourigin.argo.models.datasource;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.structure.nodes.PageInfo;

import java.util.List;
import java.util.Map;

public interface DataSource<T extends DataSourceQuery> {
    String getType();
    List<ContentElement> generateContent(PageInfo ownerPage, DataSourceIdentifier id, T query, Map<String, Object> context);
}

package com.fourigin.argo.repository;

import com.fourigin.argo.models.datasource.index.DataSourceIndex;

public interface DataSourceIndexResolver {
    DataSourceIndex resolveIndex(String path, String indexName);
}

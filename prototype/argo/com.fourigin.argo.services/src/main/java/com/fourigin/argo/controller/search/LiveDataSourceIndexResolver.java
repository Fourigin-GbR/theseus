package com.fourigin.argo.controller.search;

import com.fourigin.argo.models.datasource.index.DataSourceIndex;
import com.fourigin.argo.repository.DataSourceIndexResolver;

public class LiveDataSourceIndexResolver implements DataSourceIndexResolver {
    @Override
    public DataSourceIndex resolveIndex(String customer, String base, String path, String indexName) {
        return null;
    }
}

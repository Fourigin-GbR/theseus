package com.fourigin.argo.compiler.datasource;

import com.fourigin.argo.models.datasource.DataSourceQuery;

import java.util.Map;

public class EmptyDataSourceQuery implements DataSourceQuery {
    @Override
    public void buildFromMap(Map<String, Object> values) {
        // nothing to do
    }
}

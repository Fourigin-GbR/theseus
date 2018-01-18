package com.fourigin.argo.compiler.datasource;

import java.util.Map;

public class EmptyDataSourceQuery implements DataSourceQuery {
    @Override
    public void buildFromMap(Map<String, Object> values) {
        // nothing to do
    }
}

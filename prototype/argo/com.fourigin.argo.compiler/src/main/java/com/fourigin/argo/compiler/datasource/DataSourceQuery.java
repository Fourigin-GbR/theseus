package com.fourigin.argo.compiler.datasource;

import java.util.Map;

public interface DataSourceQuery {
    void buildFromMap(Map<String, Object> values);
}

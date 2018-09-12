package com.fourigin.argo.models.datasource;

import java.util.Map;

public interface DataSourceQuery {
    void buildFromMap(Map<String, Object> values);
}

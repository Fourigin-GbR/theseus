package com.fourigin.cms.compiler.datasource;

import java.util.Map;

public interface DataSourceQuery {
    void buildFromMap(Map<String, Object> values);
}

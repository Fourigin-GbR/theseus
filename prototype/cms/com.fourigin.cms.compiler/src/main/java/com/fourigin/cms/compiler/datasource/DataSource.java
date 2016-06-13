package com.fourigin.cms.compiler.datasource;

public interface DataSource {
    String getType();
    DataSourceResponse apply(DataSourceQuery query);
}

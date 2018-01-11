package com.fourigin.argo.compiler.datasource;

public interface DataSource {
    String getType();
    DataSourceResponse apply(DataSourceQuery query);
}

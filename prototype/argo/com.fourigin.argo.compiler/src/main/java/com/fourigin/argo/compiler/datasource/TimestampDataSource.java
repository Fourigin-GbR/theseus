package com.fourigin.argo.compiler.datasource;

public class TimestampDataSource implements DataSource {
    @Override
    public String getType() {
        return "TIMESTAMP";
    }

    @Override
    public DataSourceResponse apply(DataSourceQuery query) {
        return new TimestampDataSourceResponse();
    }
}

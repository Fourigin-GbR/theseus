package com.fourigin.argo.compiler.datasource;

import java.util.Map;

public class DataSourceQueryBuilder {
    private Class<? extends DataSourceQuery> baseQuery;

    public DataSourceQueryBuilder(Class<? extends DataSourceQuery> baseQuery){
        this.baseQuery = baseQuery;
    }

    public DataSourceQuery build(Map<String, Object> context){
        String queryName = baseQuery.getName();

        DataSourceQuery query;
        try {
            query = baseQuery.cast(Class.forName(queryName).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            throw new DataSourceQueryCreationException("Unable to instantiate DataSourceQuery for name '" + queryName + "'", ex);
        }

        query.buildFromMap(context);
        return query;
    }
}

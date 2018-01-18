package com.fourigin.argo.compiler.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DataSourceQueryBuilder<T extends DataSourceQuery> {
    private Class<T> baseQuery;

    private final Logger logger = LoggerFactory.getLogger(DataSourceQueryBuilder.class);

    public DataSourceQueryBuilder(Class<T> baseQuery){
        this.baseQuery = baseQuery;
    }

    public T build(Map<String, Object> context){
        String queryName = baseQuery.getName();

        T query;
        try {
            query = baseQuery.cast(Class.forName(queryName).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            if (logger.isErrorEnabled()) logger.error("Unable to instantiate DataSourceQuery for name '{}'!", queryName, ex);
            return null;
        }

        query.buildFromMap(context);
        return query;
    }
}
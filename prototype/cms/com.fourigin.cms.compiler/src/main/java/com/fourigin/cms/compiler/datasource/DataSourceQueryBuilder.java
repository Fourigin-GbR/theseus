package com.fourigin.cms.compiler.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DataSourceQueryBuilder {
    private Class<DataSourceQuery> baseQuery;

    private final Logger logger = LoggerFactory.getLogger(DataSourceQueryBuilder.class);

    public DataSourceQuery build(Map<String, Object> context){
        String queryName = baseQuery.getName();

        DataSourceQuery query;
        try {
            query = (DataSourceQuery) Class.forName(queryName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            if (logger.isErrorEnabled()) logger.error("Unable to instantiate DataSourceQuery for name '{}'!", queryName, ex);
            return null;
        }

        query.buildFromMap(context);
        return query;
    }
}
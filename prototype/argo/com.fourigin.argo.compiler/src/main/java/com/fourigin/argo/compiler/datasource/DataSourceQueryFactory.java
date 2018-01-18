package com.fourigin.argo.compiler.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public final class DataSourceQueryFactory {
    private static Map<String, DataSourceQueryBuilder<? extends DataSourceQuery>> builders;

    private DataSourceQueryFactory(){
    }
    
    static public DataSourceQuery buildFromMap(DataSource dataSource, Map<String, Object> values){
        if(builders == null || builders.isEmpty()){
            throw new IllegalStateException("DataSourceQuery builders are not defined!");
        }

        if(dataSource == null){
            throw new IllegalArgumentException("dataSource must not be null!");
        }

        Logger logger = LoggerFactory.getLogger(DataSourceQueryFactory.class);

        String type = dataSource.getType();
        DataSourceQueryBuilder<? extends DataSourceQuery> builder = builders.get(type);
        if(builder == null){
            if (logger.isErrorEnabled()) logger.error("Unable to find DataSourceQueryBuilder for type '{}'!", type);
            return null;
        }

        return builder.build(values);
    }

    public static void setBuilders(Map<String, DataSourceQueryBuilder<? extends DataSourceQuery>> builders) {
        DataSourceQueryFactory.builders = builders;
    }
}

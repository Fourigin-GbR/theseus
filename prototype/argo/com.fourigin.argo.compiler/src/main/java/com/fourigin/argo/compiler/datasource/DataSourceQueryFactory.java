package com.fourigin.argo.compiler.datasource;

import java.util.Map;
import java.util.Objects;

public final class DataSourceQueryFactory {
    private static Map<String, DataSourceQueryBuilder> builders;

    private DataSourceQueryFactory(){
    }
    
    static public DataSourceQuery buildFromMap(DataSource<DataSourceQuery> dataSource, Map<String, Object> values){
        if(builders == null || builders.isEmpty()){
            throw new DataSourceQueryCreationException("No query builders defined! Check configuration!", values);
        }

        Objects.requireNonNull(dataSource, "dataSource must not be null!");

        String type = dataSource.getType();
        DataSourceQueryBuilder builder = builders.get(type);
        if(builder == null){
            throw new DataSourceQueryCreationException("No builder found for type '" + type + "'!", values);
        }

        return builder.build(values);
    }

    public static void setBuilders(Map<String, DataSourceQueryBuilder> builders) {
        DataSourceQueryFactory.builders = builders;
    }
}

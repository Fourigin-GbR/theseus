package com.fourigin.argo.compiler.datasource;

import java.util.Map;

public class DataSourceQueryCreationException extends DataSourceResolvingException {

    public DataSourceQueryCreationException(String message, String type, Map<String, Object> queryParameters){
        super(message + " : error creating a dataSource query for type '" + type + "' and based on parameters " + queryParameters + "'");
    }

    public DataSourceQueryCreationException(String message, Map<String, Object> queryParameters){
        super(message + " : error creating a dataSource query based on parameters " + queryParameters + "'");
    }

    public DataSourceQueryCreationException(String message, Throwable cause){
        super(message, cause);
    }
}

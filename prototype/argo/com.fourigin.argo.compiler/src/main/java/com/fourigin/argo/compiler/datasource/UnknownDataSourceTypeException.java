package com.fourigin.argo.compiler.datasource;

public class UnknownDataSourceTypeException extends DataSourceResolvingException {
    private String type;

    public UnknownDataSourceTypeException(String type){
        super("No dataSource found for type '" + type + "'!");

        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "UnknownDataSourceTypeException{" +
            "type='" + type + '\'' +
            '}';
    }
}

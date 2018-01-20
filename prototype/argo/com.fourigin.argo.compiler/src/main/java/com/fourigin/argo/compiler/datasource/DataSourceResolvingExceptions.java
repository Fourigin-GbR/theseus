package com.fourigin.argo.compiler.datasource;

import java.util.List;

public class DataSourceResolvingExceptions extends IllegalStateException {
    private List<DataSourceResolvingException> exceptions;

    public DataSourceResolvingExceptions(List<DataSourceResolvingException> exceptions) {
        super(buildErrorMessage(exceptions));

        this.exceptions = exceptions;
    }

    private static String buildErrorMessage(List<DataSourceResolvingException> exceptions){
        StringBuilder builder = new StringBuilder();

        if(exceptions != null && !exceptions.isEmpty()){
            for (DataSourceResolvingException exception : exceptions) {
                if(builder.length() > 0){
                    builder.append(", ");
                }
                builder.append(exception.getMessage());
            }
        }
        builder.insert(0, "Errors occurred while resolving dataSources: ");
        
        return builder.toString();
    }

    public List<DataSourceResolvingException> getExceptions() {
        return exceptions;
    }

    @Override
    public String toString() {
        return "DataSourceResolvingExceptions{" +
            "exceptions=" + exceptions +
            '}';
    }
}

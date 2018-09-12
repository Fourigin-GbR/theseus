package com.fourigin.argo.models.datasource;

public class DataSourceResolvingException extends IllegalStateException {
    public DataSourceResolvingException() {
        super();
    }

    public DataSourceResolvingException(String msg) {
        super(msg);
    }

    public DataSourceResolvingException(Throwable cause) {
        super(cause);
    }

    public DataSourceResolvingException(String message, Throwable cause) {
        super(message, cause);
    }
}

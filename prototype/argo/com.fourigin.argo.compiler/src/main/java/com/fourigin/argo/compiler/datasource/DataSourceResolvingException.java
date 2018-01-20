package com.fourigin.argo.compiler.datasource;

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

package com.fourigin.argo.controller;

public class ResponseStatus {
    public static final int SUCCESS = 0;
    public static final int UNMODIFIED = 1;

    public static final int SUCCESSFUL_CREATE = 10;
    public static final int SUCCESSFUL_UPDATE = 11;
    public static final int SUCCESSFUL_DELETE = 12;

    public static final int FAILED_CONCURRENT_UPDATE = 1000;
    public static final int FAILED_NOT_WRITABLE = 1110;
}

package com.fourigin.argo.controller;

public enum ServiceResponseStatus {

    SUCCESS(0),

    SUCCESSFUL_CREATE(10),
    SUCCESSFUL_UPDATE(20),
    SUCCESSFUL_UPDATE_UNMODIFIED(21),
    SUCCESSFUL_DELETE(30),

    FAILED_CONCURRENT_UPDATE(1000),
    FAILED_NOT_READABLE(1100),
    FAILED_NOT_WRITABLE(1101),
    FAILED_UNEXPECTED_ERROR(10000);

    private int code;

    ServiceResponseStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

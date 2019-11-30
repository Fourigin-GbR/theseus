package com.fourigin.argo.controller.editors.models;

public class ApplyActionStatus {
    private boolean failed;

    private String message;

    public static ApplyActionStatus SUCCESS() {
        return SUCCESS(null);
    }

    public static ApplyActionStatus SUCCESS(String message) {
        return new ApplyActionStatus(false, message);
    }

    public static ApplyActionStatus FAILED(String message) {
        return new ApplyActionStatus(true, message);
    }

    private ApplyActionStatus(boolean failed, String message) {
        this.failed = failed;
        this.message = message;
    }

    public boolean isFailed() {
        return failed;
    }

    public String getMessage() {
        return message;
    }
}

package com.fourigin.argo.controller.editors.models;

public final class ApplyActionStatus {
    private boolean failed;

    private String message;

    public static ApplyActionStatus success() {
        return success(null);
    }

    public static ApplyActionStatus success(String message) {
        return new ApplyActionStatus(false, message);
    }

    public static ApplyActionStatus fail(String message) {
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

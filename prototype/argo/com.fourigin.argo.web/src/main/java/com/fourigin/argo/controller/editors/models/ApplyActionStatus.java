package com.fourigin.argo.controller.editors.models;

public class ApplyActionStatus {
    private boolean failed;

    private String message;

    public ApplyActionStatus(boolean failed, String message) {
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

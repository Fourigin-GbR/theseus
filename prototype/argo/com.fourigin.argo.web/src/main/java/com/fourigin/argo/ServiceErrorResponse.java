package com.fourigin.argo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceErrorResponse {
    private int statusCode;
    private String title;
    private String message;
    private List<String> cause;

    private static final int DEFAULT_STATUS_CODE = 500;

    public ServiceErrorResponse(int statusCode, String title, String message, Throwable cause){
        this.statusCode = statusCode;
        this.title = title;
        this.message = message;

        if(cause != null) {
            this.cause = new ArrayList<>();

            Throwable c = cause;
            while(c != null) {
                this.cause.add(c.getMessage());
                c = c.getCause();
            }
        }
    }

    public ServiceErrorResponse(int statusCode, String title, String message, String cause){
        this.statusCode = statusCode;
        this.title = title;
        this.message = message;
        this.cause = Collections.singletonList(cause);
    }

    public ServiceErrorResponse(int statusCode, String title, String message){
        this(statusCode, title, message, (String) null);
    }

    public ServiceErrorResponse(String title, String message){
        this(DEFAULT_STATUS_CODE, title, message, (String) null);
    }

    public ServiceErrorResponse(String title, String message, String cause){
        this(DEFAULT_STATUS_CODE, title, message, cause);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getCause() {
        return cause;
    }
}

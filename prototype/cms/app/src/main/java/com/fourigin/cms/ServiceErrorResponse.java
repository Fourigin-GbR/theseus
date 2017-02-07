package com.fourigin.cms;

public class ServiceErrorResponse {
    private int statusCode;
    private String title;
    private String message;
    private String cause;

    private static final int DEFAULT_STATUS_CODE = 500;

    public ServiceErrorResponse(int statusCode, String title, String message, Throwable cause){
        this.statusCode = statusCode;
        this.title = title;
        this.message = message;

        if(cause != null) {
            StringBuilder builder = new StringBuilder();

            Throwable c = cause;
            while(c != null) {
                if (builder.length() > 0) {
                    builder.append(" --- ");
                }

                builder.append(c.getMessage());

                c = c.getCause();
            }

            this.cause = builder.toString();
        }
    }

    public ServiceErrorResponse(int statusCode, String title, String message, String cause){
        this.statusCode = statusCode;
        this.title = title;
        this.message = message;
        this.cause = cause;
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

    public String getCause() {
        return cause;
    }
}

package com.fourigin.cms.models.content;

public class UnresolvableContentPathException extends RuntimeException {
    private String path;

    public UnresolvableContentPathException(String message, String path) {
        super(message);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "UnresolvableContentPathException{" +
            "path='" + path + '\'' +
            '}';
    }
}

package com.fourigin.argo.repository;

public class UnresolvableSiteStructurePathException extends RuntimeException {
    private static final long serialVersionUID = -7314616630285303692L;

    private String path;

    private String unresolvableToken;

    public UnresolvableSiteStructurePathException(String token, String path) {
        super("Unable to resolve token '" + token + "' in path '" + path + "'!");

        this.path = path;
        this.unresolvableToken = token;
    }

    public String getPath() {
        return path;
    }

    public String getUnresolvableToken() {
        return unresolvableToken;
    }
}

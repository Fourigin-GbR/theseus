package com.fourigin.argo.repository;

public class UnresolvableSiteStructurePathException extends RuntimeException {
    private static final long serialVersionUID = -7314616630285303692L;

    public UnresolvableSiteStructurePathException(String token, String path) {
        super("Unable to resolve token '" + token + "' in path '" + path + "'!");
    }
}

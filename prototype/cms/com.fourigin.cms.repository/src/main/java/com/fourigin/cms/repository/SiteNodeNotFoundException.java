package com.fourigin.cms.repository;

public class SiteNodeNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -7314616630285303692L;

    public SiteNodeNotFoundException(String token, String path) {
        super("Unable to resolve token '" + token + "' in path '" + path + "'!");
    }
}

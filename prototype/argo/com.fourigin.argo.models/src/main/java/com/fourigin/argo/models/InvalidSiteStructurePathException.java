package com.fourigin.argo.models;

public class InvalidSiteStructurePathException extends RuntimeException {

    private static final long serialVersionUID = -6568058474850779502L;

    private String path;
    private String unresolvableToken;

    public InvalidSiteStructurePathException(String path) {
        super("Invalid site structure path '" + path + "'!");
        this.path = path;
    }

    public InvalidSiteStructurePathException(String path, String unknownToken) {
        super("Invalid site structure path! No token '" + unknownToken + "' found in path '" + path + "'!");
        this.path = path;
        this.unresolvableToken = unknownToken;
    }

    public String getPath() {
        return path;
    }

    public String getUnresolvableToken() {
        return unresolvableToken;
    }
}

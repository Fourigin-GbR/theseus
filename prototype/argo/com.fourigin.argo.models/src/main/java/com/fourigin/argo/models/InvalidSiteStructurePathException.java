package com.fourigin.argo.models;

public class InvalidSiteStructurePathException extends RuntimeException {
    private static final long serialVersionUID = -6568058474850779502L;

    public InvalidSiteStructurePathException(String path) {
        super("Invalid site structure path '" + path + "'!");
    }

    public InvalidSiteStructurePathException(String path, String unknownToken) {
        super("Invalid site structure path! No token '" + unknownToken + "' found in path '" + path + "'!");
    }

    public InvalidSiteStructurePathException(String path, Class requiredClass, Class foundClass) {
        super("Invalid site structure path! Found a node of type '" + foundClass + "', but required '" + requiredClass + "' for path '" + path + "'!");
    }
}

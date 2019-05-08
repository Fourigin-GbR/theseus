package com.fourigin.argo.models;

public class InvalidSiteStructureNodeException extends RuntimeException {

    private static final long serialVersionUID = -8936249125498202237L;

    private String path;
    private Class requiredClass;
    private Class foundClass;

    public InvalidSiteStructureNodeException(String path, Class requiredClass, Class foundClass) {
        super("Invalid site structure path! Found a node of type '" + foundClass + "', but required '" + requiredClass + "' for path '" + path + "'!");
        this.path = path;
        this.foundClass = foundClass;
        this.requiredClass = requiredClass;
    }

    public String getPath() {
        return path;
    }

    public Class getRequiredClass() {
        return requiredClass;
    }

    public Class getFoundClass() {
        return foundClass;
    }
}

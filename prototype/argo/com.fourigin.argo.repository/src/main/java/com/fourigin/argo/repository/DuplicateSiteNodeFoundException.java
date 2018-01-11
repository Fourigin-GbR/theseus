package com.fourigin.argo.repository;

public class DuplicateSiteNodeFoundException extends RuntimeException {

    private static final long serialVersionUID = 65215303051929533L;

    public DuplicateSiteNodeFoundException(String path, String name) {
        super("Found a duplicate of a SiteNode with name '" + name + "' in path '" + path + "'!");
    }
}

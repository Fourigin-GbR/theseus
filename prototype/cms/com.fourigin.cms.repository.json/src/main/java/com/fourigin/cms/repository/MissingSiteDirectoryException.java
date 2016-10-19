package com.fourigin.cms.repository;

import com.fourigin.cms.models.structure.nodes.SiteNode;
import com.sun.istack.internal.NotNull;

import java.util.Objects;

public class MissingSiteDirectoryException extends RuntimeException {

    private static final long serialVersionUID = 3964179499855539147L;

    public MissingSiteDirectoryException(String name, SiteNode node) {
        super("Expecting a SiteDirectory for name '" + name + "', but found '" + (node == null? "null" : node.getClass().getName()) + "'!");
    }
}

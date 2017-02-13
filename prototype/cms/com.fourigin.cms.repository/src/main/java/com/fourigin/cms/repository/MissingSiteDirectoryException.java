package com.fourigin.cms.repository;

import com.fourigin.cms.models.structure.nodes.SiteNodeInfo;

public class MissingSiteDirectoryException extends RuntimeException {

    private static final long serialVersionUID = 3964179499855539147L;

    public MissingSiteDirectoryException(String name, SiteNodeInfo node) {
        super("Expecting a SiteDirectory for name '" + name + "', but found '" + (node == null? "null" : node.getClass().getName()) + "'!");
    }
}

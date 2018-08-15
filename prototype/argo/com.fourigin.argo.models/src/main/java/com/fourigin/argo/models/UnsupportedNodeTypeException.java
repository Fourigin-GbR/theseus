package com.fourigin.argo.models;

import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

public class UnsupportedNodeTypeException extends RuntimeException {
    private static final long serialVersionUID = 5421332272172512529L;

    public UnsupportedNodeTypeException(Class<? extends SiteNodeInfo> nodeClass) {
        super("Unsupported node of type '" + nodeClass.getName());
    }
}

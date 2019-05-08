package com.fourigin.argo.models;

import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

public class UnsupportedNodeTypeException extends RuntimeException {
    private static final long serialVersionUID = 5421332272172512529L;

    private String nodeName;

    public UnsupportedNodeTypeException(Class<? extends SiteNodeInfo> nodeClass) {
        super("Unsupported node of type '" + nodeClass.getName());
        this.nodeName = nodeClass.getName();
    }

    public String getNodeName() {
        return nodeName;
    }
}

package com.fourigin.argo.models.structure.nodes;

import java.util.List;

public interface SiteNodeContainerInfo extends SiteNodeInfo {
    List<SiteNodeInfo> getNodes();
    void setNodes(List<SiteNodeInfo> nodes);

    SiteNodeInfo getDefaultTarget();
}

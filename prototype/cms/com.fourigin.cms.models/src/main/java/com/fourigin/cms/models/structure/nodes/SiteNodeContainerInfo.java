package com.fourigin.cms.models.structure.nodes;

import java.util.List;

public interface SiteNodeContainerInfo {
    String getPath();
    void setPath(String path);

    List<SiteNodeInfo> getNodes();

    void setNodes(List<SiteNodeInfo> nodes);
}
package com.fourigin.cms.models.structure.nodes;

import java.util.List;

public interface SiteNodeContainer {
    String getPath();
    void setPath(String path);

    List<SiteNode> getNodes();

    void setNodes(List<SiteNode> nodes);
}
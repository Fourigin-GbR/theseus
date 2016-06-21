package com.fourigin.cms.models.structure.nodes;

import java.util.List;

public interface SiteNodeContainer {
    List<SiteNode> getNodes();

    void setNodes(List<SiteNode> nodes);
}
package com.fourigin.cms.models.structure.nodes;

import java.util.List;

public class SiteDirectory extends AbstractSiteNode implements SiteNode , SiteNodeContainer {
    private List<SiteNode> nodes;

    public List<SiteNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<SiteNode> nodes) {
        this.nodes = nodes;
    }
}

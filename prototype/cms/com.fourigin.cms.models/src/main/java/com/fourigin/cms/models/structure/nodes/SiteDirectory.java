package com.fourigin.cms.models.structure.nodes;

import java.util.List;

public class SiteDirectory extends AbstractSiteNode implements SiteNodeContainer, SiteNode {
    private List<SiteNode> nodes;
    private boolean lazy = true;

    public List<SiteNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<SiteNode> nodes) {
        this.nodes = nodes;
    }
}

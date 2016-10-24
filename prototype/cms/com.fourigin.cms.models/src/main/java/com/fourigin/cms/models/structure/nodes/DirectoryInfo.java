package com.fourigin.cms.models.structure.nodes;

import java.util.List;

public class DirectoryInfo extends AbstractSiteNodeInfo implements SiteNodeInfo, SiteNodeInfoContainer {
    private List<SiteNodeInfo> nodes;

    public List<SiteNodeInfo> getNodes() {
        return nodes;
    }

    public void setNodes(List<SiteNodeInfo> nodes) {
        this.nodes = nodes;
    }
}

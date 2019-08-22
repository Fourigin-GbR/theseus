package com.fourigin.argo.repository.strategies;

import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

import java.util.Collection;

public class AllNodesTraversingStrategy implements TraversingStrategy<SiteNodeInfo, SiteNodeContainerInfo> {
    @Override
    public Collection<SiteNodeInfo> collect(SiteNodeContainerInfo container) {
        return container.getNodes();
    }
}

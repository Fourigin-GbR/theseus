package com.fourigin.argo.repository.strategies;

import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

import java.util.ArrayList;
import java.util.Collection;

public class NonRecursiveSiteNodeTraversingStrategy implements TraversingStrategy<SiteNodeInfo, SiteNodeContainerInfo> {

    @Override
    public Collection<SiteNodeInfo> collect(SiteNodeContainerInfo container) {
        Collection<SiteNodeInfo> result = new ArrayList<>();

        if(container == null || container.getNodes() == null || container.getNodes().isEmpty()){
            return result;
        }

        result.addAll(container.getNodes());

        return result;
    }
}

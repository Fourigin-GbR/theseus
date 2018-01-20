package com.fourigin.argo.repository.strategies;

import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.PageInfo;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultPageInfoTraversingStrategy implements PageInfoTraversingStrategy {

    @Override
    public Collection<PageInfo> collect(SiteNodeContainerInfo container) {
        Collection<PageInfo> result = new ArrayList<>();

        if(container == null){
            return result;
        }

        processContainer(container, result);

        return result;
    }

    private void processContainer(SiteNodeContainerInfo container, Collection<PageInfo> result){
        if(container == null || container.getNodes() == null || container.getNodes().isEmpty()){
            return;
        }

        for (SiteNodeInfo node : container.getNodes()) {
            if(node instanceof SiteNodeContainerInfo){
                processContainer((SiteNodeContainerInfo) node, result);
                continue;
            }

            if(node instanceof PageInfo){
                result.add((PageInfo) node);
                continue;
            }

            throw new IllegalArgumentException("Unsupported SiteNode type '" + node.getClass().getName() + "'!");
        }
    }
}

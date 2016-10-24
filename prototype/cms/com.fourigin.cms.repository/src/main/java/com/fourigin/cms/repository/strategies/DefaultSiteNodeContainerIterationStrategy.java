package com.fourigin.cms.repository.strategies;

import com.fourigin.cms.models.structure.nodes.SiteNodeInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeInfoContainer;
import com.fourigin.cms.models.structure.nodes.PageInfo;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultSiteNodeContainerIterationStrategy implements TraversingStrategy<PageInfo, SiteNodeInfoContainer> {

    @Override
    public Collection<PageInfo> collect(SiteNodeInfoContainer container) {
        Collection<PageInfo> result = new ArrayList<>();

        if(container == null){
            return result;
        }

        processContainer(container, result);

        return result;
    }

    private void processContainer(SiteNodeInfoContainer container, Collection<PageInfo> result){
        if(container == null || container.getNodes() == null || container.getNodes().isEmpty()){
            return;
        }

        for (SiteNodeInfo node : container.getNodes()) {
            if(node instanceof SiteNodeInfoContainer){
                processContainer((SiteNodeInfoContainer) node, result);
            }
            else if(node instanceof PageInfo){
                result.add((PageInfo) node);
            }
            else {
                throw new IllegalArgumentException("Unsupported SiteNode type '" + node.getClass().getName() + "'!");
            }
        }
    }
}
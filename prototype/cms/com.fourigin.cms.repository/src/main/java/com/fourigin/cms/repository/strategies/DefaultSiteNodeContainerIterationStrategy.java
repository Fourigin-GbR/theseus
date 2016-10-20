package com.fourigin.cms.repository.strategies;

import com.fourigin.cms.models.structure.nodes.SiteNode;
import com.fourigin.cms.models.structure.nodes.SiteNodeContainer;
import com.fourigin.cms.models.structure.nodes.SitePage;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultSiteNodeContainerIterationStrategy implements TraversingStrategy<SitePage, SiteNodeContainer> {

    @Override
    public Collection<SitePage> collect(SiteNodeContainer container) {
        Collection<SitePage> result = new ArrayList<>();

        if(container == null){
            return result;
        }

        processContainer(container, result);

        return result;
    }

    private void processContainer(SiteNodeContainer container, Collection<SitePage> result){
        if(container == null || container.getNodes() == null || container.getNodes().isEmpty()){
            return;
        }

        for (SiteNode node : container.getNodes()) {
            if(node instanceof SiteNodeContainer){
                processContainer((SiteNodeContainer) node, result);
            }
            else if(node instanceof SitePage){
                result.add((SitePage) node);
            }
            else {
                throw new IllegalArgumentException("Unsupported SiteNode type '" + node.getClass().getName() + "'!");
            }
        }
    }
}
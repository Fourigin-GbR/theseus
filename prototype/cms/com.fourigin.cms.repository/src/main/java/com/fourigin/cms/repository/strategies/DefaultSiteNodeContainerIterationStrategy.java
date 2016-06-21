package com.fourigin.cms.repository.strategies;

import com.fourigin.cms.models.structure.nodes.SiteNode;
import com.fourigin.cms.models.structure.nodes.SiteNodeContainer;
import com.fourigin.cms.models.structure.nodes.SitePage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class DefaultSiteNodeContainerIterationStrategy implements IterationStrategy<SitePage, SiteNodeContainer> {

    @Override
    public Iterator<SitePage> createIteratorOver(SiteNodeContainer container) {
        Collection<SitePage> result = new ArrayList<>();

        if(container == null){
            return result.iterator();
        }

        processContainer(container, result);

        return result.iterator();
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
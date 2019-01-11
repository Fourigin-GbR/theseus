package com.fourigin.argo.repository.strategies;

import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PatternMatchingSiteNodeTraversingStrategy implements TraversingStrategy<SiteNodeInfo, SiteNodeContainerInfo> {

    private String pattern;

    private boolean recursive;

    public PatternMatchingSiteNodeTraversingStrategy(String pattern, boolean recursive) {
        this.pattern = pattern;
        this.recursive = recursive;
    }

    @Override
    public Collection<SiteNodeInfo> collect(SiteNodeContainerInfo container) {
        Collection<SiteNodeInfo> result = new ArrayList<>();

        if (container == null || container.getNodes() == null || container.getNodes().isEmpty()) {
            return result;
        }

        if (recursive) {
            processContainer(container, result);
            return result;
        }

        List<SiteNodeInfo> nodes = container.getNodes();
        for (SiteNodeInfo node : nodes) {
            String nodeName = node.getName();
            if (nodeName.matches(pattern)) {
                result.add(node);
            }
        }

        return result;
    }

    private void processContainer(SiteNodeContainerInfo container, Collection<SiteNodeInfo> result) {
        if (container == null || container.getNodes() == null || container.getNodes().isEmpty()) {
            return;
        }

        for (SiteNodeInfo node : container.getNodes()) {
            if (node instanceof SiteNodeContainerInfo) {
                SiteNodeContainerInfo subContainer = (SiteNodeContainerInfo) node;
                processContainer(subContainer, result);
                continue;
            }

            if (node instanceof PageInfo) {
                String nodeName = node.getName();
                if (nodeName.matches(pattern)) {
                    result.add(node);
                }
                continue;
            }

            throw new IllegalArgumentException("Unsupported SiteNode type '" + node.getClass().getName() + "'!");
        }
    }
}

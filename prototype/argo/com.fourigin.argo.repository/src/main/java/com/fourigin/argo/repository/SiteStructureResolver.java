package com.fourigin.argo.repository;

import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.repository.strategies.TraversingStrategy;

import java.util.Collection;
import java.util.Map;

@Deprecated
public interface SiteStructureResolver {
    Map<String, String> resolveSiteAttributes();

    <T extends SiteNodeInfo> T resolveNode(Class<T> type, String path);
    <T extends SiteNodeInfo> T resolveNode(Class<T> type, SiteNodeContainerInfo parent, String path);

    Collection<PageInfo> resolveNodes(String path);
    Collection<PageInfo> resolveNodes(String path, TraversingStrategy<PageInfo, SiteNodeInfo> iterationStrategy);
    Collection<PageInfo> resolveNodes(SiteNodeContainerInfo parent, String path);
    Collection<PageInfo> resolveNodes(SiteNodeContainerInfo parent, String path, TraversingStrategy<PageInfo, SiteNodeInfo> iterationStrategy);
}

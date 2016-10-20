package com.fourigin.cms.repository;

import com.fourigin.cms.models.structure.nodes.SiteNode;
import com.fourigin.cms.models.structure.nodes.SiteNodeContainer;
import com.fourigin.cms.models.structure.nodes.SitePage;
import com.fourigin.cms.repository.strategies.TraversingStrategy;

import java.util.Collection;
import java.util.Map;

public interface SiteStructureResolver {
    Map<String, String> resolveSiteAttributes();

    <T extends SiteNode> T resolveNode(Class<T> type, String path);
    <T extends SiteNode> T resolveNode(Class<T> type, SiteNodeContainer parent, String path);

    Collection<SitePage> resolveNodes(String path);
    Collection<SitePage> resolveNodes(String path, TraversingStrategy<SitePage, SiteNode> iterationStrategy);
    Collection<SitePage> resolveNodes(SiteNodeContainer parent, String path);
    Collection<SitePage> resolveNodes(SiteNodeContainer parent, String path, TraversingStrategy<SitePage, SiteNode> iterationStrategy);
}
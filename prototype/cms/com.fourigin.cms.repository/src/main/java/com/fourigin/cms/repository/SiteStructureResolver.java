package com.fourigin.cms.repository;

import com.fourigin.cms.models.structure.nodes.SiteNode;
import com.fourigin.cms.models.structure.nodes.SiteNodeContainer;
import com.fourigin.cms.models.structure.nodes.SitePage;
import com.fourigin.cms.repository.strategies.IterationStrategy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface SiteStructureResolver {
    Map<String, String> resolveSiteAttributes();

    <T extends SiteNode> T resolveNode(Class<T> type, String path);
    <T extends SiteNode> T resolveNode(Class<T> type, SiteNodeContainer parent, String path);

    Iterator<SitePage> resolveIterator(String path);
    Iterator<SitePage> resolveIterator(String path, IterationStrategy<SitePage, SiteNode> iterationStrategy);
    Iterator<SitePage> resolveIterator(SiteNodeContainer parent, String path);
    Iterator<SitePage> resolveIterator(SiteNodeContainer parent, String path, IterationStrategy<SitePage, SiteNode> iterationStrategy);
}
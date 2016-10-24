package com.fourigin.cms.repository;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeInfoContainer;
import com.fourigin.cms.repository.strategies.TraversingStrategy;

import java.util.Collection;
import java.util.Map;

public interface ContentResolver {
    Map<String, String> resolveSiteAttributes();

    <T extends SiteNodeInfo> T resolveInfo(Class<T> type, String path);
    <T extends SiteNodeInfo> T resolveInfo(Class<T> type, SiteNodeInfoContainer parent, String path);

    Collection<PageInfo> resolveInfos(String path);
    Collection<PageInfo> resolveInfos(String path, TraversingStrategy<PageInfo, SiteNodeInfo> iterationStrategy);
    Collection<PageInfo> resolveInfos(SiteNodeInfoContainer parent, String path);
    Collection<PageInfo> resolveInfos(SiteNodeInfoContainer parent, String path, TraversingStrategy<PageInfo, SiteNodeInfo> iterationStrategy);

    ContentPage retrieve(PageInfo info);
}

package com.fourigin.argo.repository;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;

import java.io.Flushable;
import java.util.Collection;
import java.util.Map;

public interface ContentResolver extends Flushable {
    Map<String, String> resolveSiteAttributes();

    <T extends SiteNodeInfo> T resolveInfo(Class<T> type, String path);
    <T extends SiteNodeInfo> T resolveInfo(Class<T> type, SiteNodeContainerInfo parent, String path);

    Collection<PageInfo> resolveInfos(String path);
    Collection<PageInfo> resolveInfos(String path, PageInfoTraversingStrategy traversingStrategy);
    Collection<PageInfo> resolveInfos(SiteNodeContainerInfo parent, String path);
    Collection<PageInfo> resolveInfos(SiteNodeContainerInfo parent, String path, PageInfoTraversingStrategy traversingStrategy);

    ContentPage retrieve(PageInfo info);
}

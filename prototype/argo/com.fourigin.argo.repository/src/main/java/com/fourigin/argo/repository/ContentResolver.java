package com.fourigin.argo.repository;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.datasource.index.DataSourceIndex;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;
import com.fourigin.argo.repository.strategies.TraversingStrategy;

import java.io.Flushable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ContentResolver extends Flushable {
    String getId();

    TraversingStrategy<? extends SiteNodeInfo, SiteNodeContainerInfo> getDefaultTraversingStrategy();

    Map<String, String> resolveSiteAttributes();

    <T extends SiteNodeInfo> T resolveInfo(Class<T> type, String path);
    <T extends SiteNodeInfo> T resolveInfo(Class<T> type, SiteNodeContainerInfo parent, String path);

    PageState resolvePageState(PageInfo pageInfo);

    Collection<PageInfo> resolveInfos(String path);
    Collection<PageInfo> resolveInfos(String path, PageInfoTraversingStrategy traversingStrategy);
    Collection<PageInfo> resolveInfos(SiteNodeContainerInfo parent, String path);
    Collection<PageInfo> resolveInfos(SiteNodeContainerInfo parent, String path, PageInfoTraversingStrategy traversingStrategy);
    Collection<SiteNodeInfo> resolveNodeInfos(String path, TraversingStrategy<? extends SiteNodeInfo, SiteNodeContainerInfo> traversingStrategy);

    ContentPage retrieve(PageInfo info);

    Set<String> listIndexes(PageInfo info);
    DataSourceIndex resolveIndex(PageInfo info, String name);
}

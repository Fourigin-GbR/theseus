package com.fourigin.argo.repository;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.datasource.index.DataSourceIndex;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.repository.strategies.DefaultPageInfoTraversingStrategy;
import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;
import com.fourigin.argo.repository.strategies.TraversingStrategy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ContentRepositoryStub implements ContentRepository {
    private Map<String, String> siteAttributes;

    private Map<String, SiteNodeInfo> infos;

    private Map<PageInfo, ContentPage> pages;

    private Map<PageInfo, PageState> states;

    private Map<PageInfo, Map<String, DataSourceIndex>> indexes;

    private PageInfoTraversingStrategy defaultTraversingStrategy = new DefaultPageInfoTraversingStrategy();

    public void flush() {
        // nothing to do!
    }

    public ContentRepositoryStub(
        Map<String, String> siteAttributes,
        Map<String, SiteNodeInfo> infos,
        Map<PageInfo, ContentPage> pages,
        Map<PageInfo, PageState> states,
        Map<PageInfo, Map<String, DataSourceIndex>> indexes
    ) {
        this.siteAttributes = siteAttributes;
        this.infos = infos;
        this.pages = pages;
        this.states = states;
        this.indexes = indexes;
    }

    @Override
    public TraversingStrategy<? extends SiteNodeInfo, SiteNodeContainerInfo> getDefaultTraversingStrategy() {
        return defaultTraversingStrategy;
    }

    @Override
    public Map<String, String> resolveSiteAttributes() {
        return siteAttributes;
    }

    @Override
    public void updateSiteStructureAttributes(Map<String, String> attributes) {
        this.siteAttributes = new HashMap<>(attributes);
    }

    @Override
    public <T extends SiteNodeInfo> T resolveInfo(Class<T> type, String path) {
        SiteNodeInfo info = infos.get(path);
        if (info == null) {
            return null;
        }

        if (type.isAssignableFrom(info.getClass())) {
            return type.cast(infos.get(path));
        }

        throw new IllegalArgumentException("Unable to cast " + info.getClass().getName() + " to " + type.getName() + "!");
    }

    @Override
    public <T extends SiteNodeInfo> T resolveInfo(Class<T> type, SiteNodeContainerInfo parent, String path) {
        String fullPath = parent.getPath() + path;
        return resolveInfo(type, fullPath);
    }

    @Override
    public Collection<PageInfo> resolveInfos(String path) {
        return resolveInfos(path, defaultTraversingStrategy);
    }

    @Override
    public Collection<PageInfo> resolveInfos(String path, PageInfoTraversingStrategy traversingStrategy) {
        SiteNodeInfo info = infos.get(path);
        if (info == null) {
            return null;
        }

        if (PageInfo.class.isAssignableFrom(info.getClass())) {
            return Collections.singletonList(PageInfo.class.cast(info));
        }

        return traversingStrategy.collect(SiteNodeContainerInfo.class.cast(info));
    }

    @Override
    public Collection<PageInfo> resolveInfos(SiteNodeContainerInfo parent, String path) {
        return resolveInfos(parent, path, defaultTraversingStrategy);
    }

    @Override
    public Collection<PageInfo> resolveInfos(SiteNodeContainerInfo parent, String path, PageInfoTraversingStrategy traversingStrategy) {
        String fullPath = parent.getPath() + path;
        return resolveInfos(fullPath, traversingStrategy);
    }

    @Override
    public Collection<SiteNodeInfo> resolveNodeInfos(String path, TraversingStrategy<? extends SiteNodeInfo, SiteNodeContainerInfo> traversingStrategy) {
        // TODO: implement me!
        return null;
    }

    @Override
    public void createInfo(String path, SiteNodeInfo node) {
        infos.put(path, node);
    }

    @Override
    public void createInfo(SiteNodeContainerInfo parent, SiteNodeInfo node) {
        String path = parent.getPath();
        infos.put(path, node);
    }

    @Override
    public void updateInfo(String path, SiteNodeInfo node) {
        infos.put(path, node);
    }

    @Override
    public void updateInfo(SiteNodeContainerInfo parent, SiteNodeInfo node) {
        String path = parent.getPath();
        infos.put(path, node);
    }

    @Override
    public void deleteInfo(String path) {
        infos.remove(path);
    }

    @Override
    public void deleteInfo(SiteNodeContainerInfo parent, String path) {
        String fullPath = parent.getPath() + path;
        infos.remove(fullPath);
    }

    @Override
    public ContentPage retrieve(PageInfo info) {
        return pages.get(info);
    }

    @Override
    public void create(PageInfo info, ContentPage contentPage) {
        pages.put(info, contentPage);
    }

    @Override
    public void update(PageInfo info, ContentPage contentPage) {
        pages.put(info, contentPage);
    }

    @Override
    public void delete(PageInfo info) {
        pages.remove(info);
    }

    @Override
    public void createPageState(PageInfo pageInfo, PageState pageState) {
        states.put(pageInfo, pageState);
    }

    @Override
    public void updatePageState(PageInfo pageInfo, PageState pageState) {
        states.put(pageInfo, pageState);
    }

    @Override
    public void deletePageState(PageInfo pageInfo) {
        states.remove(pageInfo);
    }

    @Override
    public PageState resolvePageState(PageInfo pageInfo) {
        return states.get(pageInfo);
    }

    @Override
    public void createIndex(PageInfo info, DataSourceIndex index) {
        Map<String, DataSourceIndex> map = indexes.get(info);
        if (map == null) {
            map = new HashMap<>();
            indexes.put(info, map);
        }

        map.put(index.getName(), index);
    }

    @Override
    public void deleteIndex(PageInfo info, String indexName) {
        Map<String, DataSourceIndex> map = indexes.get(info);
        if (map != null) {
            map.remove(indexName);
        }
    }

    @Override
    public Set<String> listIndexes(PageInfo info) {
        Map<String, DataSourceIndex> map = indexes.get(info);
        if (map != null) {
            return map.keySet();
        }

        return Collections.emptySet();
    }

    @Override
    public DataSourceIndex resolveIndex(PageInfo info, String name) {
        Map<String, DataSourceIndex> map = indexes.get(info);
        if (map != null) {
            return map.get(name);
        }

        return null;
    }
}

package com.fourigin.cms.repository;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeInfo;
import com.fourigin.cms.repository.strategies.DefaultPageInfoTraversingStrategy;
import com.fourigin.cms.repository.strategies.PageInfoTraversingStrategy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ContentRepositoryStub implements ContentRepository {
    private Map<String, String> siteAttributes;

    private Map<String, SiteNodeInfo> infos;

    private Map<PageInfo, ContentPage> pages;

    private PageInfoTraversingStrategy defaultTraversingStrategy = new DefaultPageInfoTraversingStrategy();

    public ContentRepositoryStub(Map<String, String> siteAttributes, Map<String, SiteNodeInfo> infos, Map<PageInfo, ContentPage> pages) {
        this.siteAttributes = siteAttributes;
        this.infos = infos;
        this.pages = pages;
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
        return type.cast(infos.get(path));
    }

    @Override
    public <T extends SiteNodeInfo> T resolveInfo(Class<T> type, SiteNodeContainerInfo parent, String path) {
        String fullPath = parent.getPath() + path;
        return type.cast(infos.get(fullPath));
    }

    @Override
    public Collection<PageInfo> resolveInfos(String path) {
        return resolveInfos(path, defaultTraversingStrategy);
    }

    @Override
    public Collection<PageInfo> resolveInfos(String path, PageInfoTraversingStrategy traversingStrategy) {
        SiteNodeInfo info = infos.get(path);
        if(info == null){
            return null;
        }

        if(PageInfo.class.isAssignableFrom(info.getClass())){
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

}

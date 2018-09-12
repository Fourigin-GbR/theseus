package com.fourigin.argo.repository;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.datasource.index.DataSourceIndex;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

import java.util.Map;

public interface ContentRepository extends ContentResolver {
    void updateSiteStructureAttributes(Map<String, String> attributes);

    void createInfo(String path, SiteNodeInfo node);
    void createInfo(SiteNodeContainerInfo parent, SiteNodeInfo node);

    void updateInfo(String path, SiteNodeInfo node);
    void updateInfo(SiteNodeContainerInfo parent, SiteNodeInfo node);

    void deleteInfo(String path);
    void deleteInfo(SiteNodeContainerInfo parent, String nodeName);

    void createPageState(PageInfo pageInfo, PageState pageState);
    void updatePageState(PageInfo pageInfo, PageState pageState);
    void deletePageState(PageInfo pageInfo);

    void create(PageInfo info, ContentPage contentPage);
    void update(PageInfo info, ContentPage contentPage);
    void delete(PageInfo info);

    void createIndex(PageInfo info, DataSourceIndex index);
    void deleteIndex(PageInfo info, String indexName);
}

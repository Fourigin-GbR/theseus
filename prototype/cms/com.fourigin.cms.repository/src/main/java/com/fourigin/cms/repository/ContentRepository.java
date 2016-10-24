package com.fourigin.cms.repository;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeInfoContainer;

import java.util.Map;

public interface ContentRepository extends ContentResolver {
    void updateSiteStructureAttributes(Map<String, String> attributes);

    void createInfo(String path, SiteNodeInfo node);
    void createInfo(SiteNodeInfoContainer parent, SiteNodeInfo node);

    void updateInfo(String path, SiteNodeInfo node);
    void updateInfo(SiteNodeInfoContainer parent, SiteNodeInfo node);

    void deleteInfo(String path);
    void deleteInfo(SiteNodeInfoContainer parent, String path);

    void create(PageInfo info, ContentPage contentPage);
    void update(PageInfo info, ContentPage contentPage);
    void delete(PageInfo info);
}

package com.fourigin.cms.repository;


import com.fourigin.cms.models.structure.nodes.SiteNodeInfo;
import com.fourigin.cms.models.structure.nodes.SiteNodeInfoContainer;

import java.util.Map;

@Deprecated
public interface SiteStructureRepository extends SiteStructureResolver {
    void updateSiteStructureAttributes(Map<String, String> attributes);

    void createNode(String path, SiteNodeInfo node);
    void createNode(SiteNodeInfoContainer parent, SiteNodeInfo node);

    void updateNode(String path, SiteNodeInfo node);
    void updateNode(SiteNodeInfoContainer parent, SiteNodeInfo node);

    void deleteNode(String path);
    void deleteNode(SiteNodeInfoContainer parent, String path);
}

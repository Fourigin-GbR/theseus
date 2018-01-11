package com.fourigin.argo.repository;


import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;

import java.util.Map;

@Deprecated
public interface SiteStructureRepository extends SiteStructureResolver {
    void updateSiteStructureAttributes(Map<String, String> attributes);

    void createNode(String path, SiteNodeInfo node);
    void createNode(SiteNodeContainerInfo parent, SiteNodeInfo node);

    void updateNode(String path, SiteNodeInfo node);
    void updateNode(SiteNodeContainerInfo parent, SiteNodeInfo node);

    void deleteNode(String path);
    void deleteNode(SiteNodeContainerInfo parent, String path);
}

package com.fourigin.cms.repository;


import com.fourigin.cms.models.structure.nodes.SiteNode;
import com.fourigin.cms.models.structure.nodes.SiteNodeContainer;

import java.util.Map;

public interface SiteStructureRepository extends SiteStructureResolver {
    void updateSiteStructureAttributes(Map<String, String> attributes);

    void createNode(String path, SiteNode node);
    void createNode(SiteNodeContainer parent, SiteNode node);
    void updateNode(String path, SiteNode node);
    void updateNode(SiteNodeContainer parent, SiteNode node);
    void deleteNode(String path);
    void deleteNode(SiteNodeContainer parent, String path);
}

package com.fourigin.cms.models.structure.nodes;

public interface SiteNodeInfo {
    String getPath();
    void setPath(String path);

    String getName();
    void setName(String name);

    String getLocalizedName();
    void setLocalizedName(String name);

    String getDisplayName();
    void setDisplayName(String name);

    String getDescription();
    void setDescription(String description);

    SiteNodeContainerInfo getParent();
    void setParent(SiteNodeContainerInfo siteNode);
}

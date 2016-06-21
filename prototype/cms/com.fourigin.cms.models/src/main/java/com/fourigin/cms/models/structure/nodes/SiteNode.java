package com.fourigin.cms.models.structure.nodes;

public interface SiteNode {
    String getName();
    void setName(String name);

    String getLocalizedName();
    void setLocalizedName(String name);

    String getDisplayName();
    void setDisplayName(String name);

    String getDescription();
    void setDescription(String description);

    SiteNodeContainer getParent();
    void setParent(SiteNodeContainer siteNode);
}

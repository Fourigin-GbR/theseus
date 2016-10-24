package com.fourigin.cms.models.structure.nodes;

abstract public class AbstractSiteNodeInfo implements SiteNodeInfo {
    private String path;
    private String name;
    private String localizedName;
    private String displayName;
    private String description;
    private SiteNodeInfoContainer parent;

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLocalizedName() {
        return localizedName;
    }

    @Override
    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public SiteNodeInfoContainer getParent() {
        return parent;
    }

    @Override
    public void setParent(SiteNodeInfoContainer parent) {
        this.parent = parent;
    }
}

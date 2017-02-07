package com.fourigin.cms.models.structure.nodes;

import java.util.List;

public class DirectoryInfo implements SiteNodeInfo, SiteNodeContainerInfo {
    private String path;
    private String name;
    private String localizedName;
    private String displayName;
    private String description;
    private SiteNodeContainerInfo parent;

    private List<SiteNodeInfo> nodes;

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
    public SiteNodeContainerInfo getParent() {
        return parent;
    }

    @Override
    public void setParent(SiteNodeContainerInfo parent) {
        this.parent = parent;
    }

    public List<SiteNodeInfo> getNodes() {
        return nodes;
    }

    public void setNodes(List<SiteNodeInfo> nodes) {
        this.nodes = nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirectoryInfo)) return false;
        if (!super.equals(o)) return false;

        DirectoryInfo that = (DirectoryInfo) o;

        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (localizedName != null ? !localizedName.equals(that.localizedName) : that.localizedName != null)
            return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        //noinspection SimplifiableIfStatement
        return nodes != null ? nodes.equals(that.nodes) : that.nodes == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (localizedName != null ? localizedName.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (nodes != null ? nodes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DirectoryInfo{" +
            "path='" + path + '\'' +
            ", name='" + name + '\'' +
            ", localizedName='" + localizedName + '\'' +
            ", displayName='" + displayName + '\'' +
            ", description='" + description + '\'' +
            ", nodes=" + nodes +
            '}';
    }

}

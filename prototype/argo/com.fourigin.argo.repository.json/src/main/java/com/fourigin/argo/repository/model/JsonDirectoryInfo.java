package com.fourigin.argo.repository.model;

import com.fourigin.argo.models.structure.nodes.DirectoryInfo;

public class JsonDirectoryInfo implements JsonInfo<DirectoryInfo> {
    private String path;
    private String name;
    private String localizedName;
    private String displayName;
    private String description;

    public JsonDirectoryInfo() {
    }

    public JsonDirectoryInfo(DirectoryInfo nodeInfo){
        if(nodeInfo != null) {
            this.path = nodeInfo.getPath();
            this.name = nodeInfo.getName();
            this.localizedName = nodeInfo.getLocalizedName();
            this.displayName = nodeInfo.getDisplayName();
            this.description = nodeInfo.getDescription();
        }
    }

    @Override
    public DirectoryInfo buildNodeInfo() {
        DirectoryInfo info = new DirectoryInfo();

        info.setPath(path);
        info.setName(name);
        info.setName(localizedName);
        info.setDisplayName(displayName);
        info.setDescription(description);

        return info;
    }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonDirectoryInfo)) return false;

        JsonDirectoryInfo that = (JsonDirectoryInfo) o;

        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (localizedName != null ? !localizedName.equals(that.localizedName) : that.localizedName != null)
            return false;
        //noinspection SimplifiableIfStatement
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (localizedName != null ? localizedName.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JsonDirectoryInfo{" +
            "path='" + path + '\'' +
            ", name='" + name + '\'' +
            ", localizedName='" + localizedName + '\'' +
            ", displayName='" + displayName + '\'' +
            ", description='" + description + '\'' +
            '}';
    }

}

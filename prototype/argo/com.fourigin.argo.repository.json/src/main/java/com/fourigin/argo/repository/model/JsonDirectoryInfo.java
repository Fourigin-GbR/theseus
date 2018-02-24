package com.fourigin.argo.repository.model;

import com.fourigin.argo.models.structure.nodes.DirectoryInfo;

import java.util.Objects;

public class JsonDirectoryInfo implements JsonInfo<DirectoryInfo> {
    private String name;
    private String localizedName;
    private String displayName;
    private String description;

    public JsonDirectoryInfo() {
    }

    public JsonDirectoryInfo(DirectoryInfo nodeInfo){
        if(nodeInfo != null) {
            this.name = nodeInfo.getName();
            this.localizedName = nodeInfo.getLocalizedName();
            this.displayName = nodeInfo.getDisplayName();
            this.description = nodeInfo.getDescription();
        }
    }

    @Override
    public DirectoryInfo buildNodeInfo() {
        DirectoryInfo info = new DirectoryInfo();

        info.setName(name);
        info.setLocalizedName(localizedName);
        info.setDisplayName(displayName);
        info.setDescription(description);

        return info;
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
        return Objects.equals(name, that.name) &&
            Objects.equals(localizedName, that.localizedName) &&
            Objects.equals(displayName, that.displayName) &&
            Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, localizedName, displayName, description);
    }

    @Override
    public String toString() {
        return "JsonDirectoryInfo{" +
            "name='" + name + '\'' +
            ", localizedName='" + localizedName + '\'' +
            ", displayName='" + displayName + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}

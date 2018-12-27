package com.fourigin.argo.repository.model;

import com.fourigin.argo.models.structure.nodes.DirectoryInfo;

import java.util.Map;
import java.util.Objects;

public class JsonDirectoryInfo implements JsonInfo<DirectoryInfo> {
    private String name;
    private Map<String, String> localizedName;
    private Map<String, String> displayName;
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
        return new DirectoryInfo.Builder()
            .withName(name)
            .withLocalizedName(localizedName)
            .withDisplayName(displayName)
            .withDescription(description)
            .build();
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
    public Map<String, String> getLocalizedName() {
        return localizedName;
    }

    @Override
    public void setLocalizedName(Map<String, String> localizedName) {
        this.localizedName = localizedName;
    }

    @Override
    public Map<String, String> getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(Map<String, String> displayName) {
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

package com.fourigin.argo.models.structure.nodes;

import java.util.ArrayList;
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
    public SiteNodeInfo getDefaultTarget() {
        if(nodes == null || nodes.isEmpty()){
            return null;
        }

        return nodes.get(0);
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

        DirectoryInfo that = (DirectoryInfo) o;

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
        return "DirectoryInfo{" +
            "path='" + path + '\'' +
            ", name='" + name + '\'' +
            ", localizedName='" + localizedName + '\'' +
            ", displayName='" + displayName + '\'' +
            ", description='" + description + '\'' +
            '}';
    }

    public static class Builder {
        private String path;
        private String name;
        private String localizedName;
        private String displayName;
        private String description;
        private SiteNodeContainerInfo parent;

        private List<SiteNodeInfo> nodes;

        public Builder withPath(String path){
            this.path = path;
            return this;
        }

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withLocalizedName(String localizedName){
            this.localizedName = localizedName;
            return this;
        }

        public Builder withDisplayName(String displayName){
            this.displayName = displayName;
            return this;
        }

        public Builder withDescription(String description){
            this.description = description;
            return this;
        }

        public Builder withParent(SiteNodeContainerInfo parent){
            this.parent = parent;
            return this;
        }

        public Builder withNodes(List<SiteNodeInfo> nodes){
            this.nodes = new ArrayList<>(nodes);
            return this;
        }

        public DirectoryInfo build(){
            DirectoryInfo instance = new DirectoryInfo();
            instance.setPath(path);
            instance.setName(name);
            instance.setLocalizedName(localizedName);
            instance.setDisplayName(displayName);
            instance.setDescription(description);
            instance.setParent(parent);
            instance.setNodes(nodes);
            return instance;
        }
    }
}

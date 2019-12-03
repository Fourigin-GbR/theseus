package com.fourigin.argo.models.structure.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DirectoryInfo implements SiteNodeInfo, SiteNodeContainerInfo {
    private static final long serialVersionUID = -6416204966082136735L;

    private String path;
    private String name;
    private Map<String, String> localizedName;
    private Map<String, String> displayName;
    private String description;
    private SiteNodeContainerInfo parent;

    private List<SiteNodeInfo> nodes;

    @Override
    public SiteNodeInfo getDefaultTarget() {
        if (nodes == null || nodes.isEmpty()) {
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
        return Objects.equals(path, that.path) &&
            Objects.equals(name, that.name) &&
            Objects.equals(localizedName, that.localizedName) &&
            Objects.equals(displayName, that.displayName) &&
            Objects.equals(description, that.description) &&
            Objects.equals(nodes, that.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name, localizedName, displayName, description, nodes);
    }

    @Override
    public String toString() {
        return "DirectoryInfo{" +
            "path='" + path + '\'' +
            ", name='" + name + '\'' +
            ", localizedName='" + localizedName + '\'' +
            ", displayName='" + displayName + '\'' +
            ", description='" + description + '\'' +
            ", nodes=\n\t" + nodes +
            '}';
    }

    @Override
    public String toTreeString(int depth) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < depth; i++) {
            builder.append('\t');
        }

        String indent = builder.toString();

        builder.append("DirectoryInfo{");
        if (path == null) {
            builder.append(" path=<null>");
        } else {
            builder.append(" path='").append(path).append('\'');
        }

        if (name == null) {
            builder.append(", name=<null>");
        } else {
            builder.append(", name='").append(name).append('\'');
        }

        if (localizedName == null) {
            builder.append(", localizedName=<null>");
        } else {
            builder.append(", localizedName='").append(localizedName).append('\'');
        }

        if (displayName == null) {
            builder.append(", displayName=<null>");
        } else {
            builder.append(", displayName='").append(displayName).append('\'');
        }

        if (nodes != null && !nodes.isEmpty()) {
            builder.append(", nodes=[");
            for (SiteNodeInfo node : nodes) {
                builder.append('\n').append(node.toTreeString(depth + 1));
            }
            builder.append('\n').append(indent).append(']');
        }

        builder.append('}');

        return builder.toString();
    }

    public static class Builder {
        private String path;
        private String name;
        private Map<String, String> localizedName;
        private Map<String, String> displayName;
        private String description;
        private SiteNodeContainerInfo parent;

        private List<SiteNodeInfo> nodes;

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withLocalizedName(Map<String, String> localizedName) {
            this.localizedName = localizedName;
            return this;
        }

        public Builder withDisplayName(Map<String, String> displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withParent(SiteNodeContainerInfo parent) {
            this.parent = parent;
            return this;
        }

        public Builder withNodes(List<SiteNodeInfo> nodes) {
            this.nodes = new ArrayList<>(nodes);
            return this;
        }

        public DirectoryInfo build() {
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

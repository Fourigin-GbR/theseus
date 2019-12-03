package com.fourigin.argo.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JsonInfoList {
    private String path;
    private List<JsonInfo<? extends SiteNodeInfo>> children;

    public JsonInfoList() {
    }

    public JsonInfoList(String path) {
        this.path = path;
        this.children = new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<JsonInfo<? extends SiteNodeInfo>> getChildren() {
        return children;
    }

    public void setChildren(List<JsonInfo<? extends SiteNodeInfo>> children) {
        this.children = children;

    }

    @JsonIgnore
    public Map<String, JsonInfo<? extends SiteNodeInfo>> getLookup() {
        Map<String, JsonInfo<? extends SiteNodeInfo>> lookup = new HashMap<>();

        if(children != null && !children.isEmpty()) {
            for (JsonInfo<? extends SiteNodeInfo> child : children) {
                lookup.put(child.getName(), child);
            }
        }

        return lookup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonInfoList)) return false;
        JsonInfoList that = (JsonInfoList) o;
        return Objects.equals(path, that.path) &&
            Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, children);
    }

    @Override
    public String toString() {
        return "JsonInfoList{" +
            "path='" + path + '\'' +
            ", children=" + children +
            '}';
    }
}

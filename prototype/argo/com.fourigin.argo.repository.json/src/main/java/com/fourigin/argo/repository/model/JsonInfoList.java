package com.fourigin.argo.repository.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonInfoList {
    private String path;
    private List<JsonInfo> children;

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

    public List<JsonInfo> getChildren() {
        return children;
    }

    public void setChildren(List<JsonInfo> children) {
        this.children = children;
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

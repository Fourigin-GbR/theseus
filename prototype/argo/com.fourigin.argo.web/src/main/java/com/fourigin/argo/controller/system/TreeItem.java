package com.fourigin.argo.controller.system;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class TreeItem  implements Serializable {
    private static final long serialVersionUID = -97248321128807832L;

    private String id;
    private String text;
    private String icon;
    private List<TreeItem> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<TreeItem> getChildren() {
        return children;
    }

    public void setChildren(List<TreeItem> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreeItem)) return false;
        TreeItem treeItem = (TreeItem) o;
        return Objects.equals(id, treeItem.id) &&
            Objects.equals(text, treeItem.text) &&
            Objects.equals(icon, treeItem.icon) &&
            Objects.equals(children, treeItem.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, icon, children);
    }

    @Override
    public String toString() {
        return "TreeItem{" +
            "id='" + id + '\'' +
            ", text='" + text + '\'' +
            ", icon='" + icon + '\'' +
            ", children=" + children +
            '}';
    }
}

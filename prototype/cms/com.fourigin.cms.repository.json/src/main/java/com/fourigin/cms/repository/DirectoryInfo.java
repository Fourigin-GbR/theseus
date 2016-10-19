package com.fourigin.cms.repository;

import java.util.Map;

public class DirectoryInfo {
    private String parentPath;

    private Map<String, DirectoryInfoItem> items;

    private String defaultItem;

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public Map<String, DirectoryInfoItem> getItems() {
        return items;
    }

    public void setItems(Map<String, DirectoryInfoItem> items) {
        this.items = items;
    }

    public String getDefaultItem() {
        return defaultItem;
    }

    public void setDefaultItem(String defaultItem) {
        this.defaultItem = defaultItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirectoryInfo)) return false;

        DirectoryInfo that = (DirectoryInfo) o;

        if (parentPath != null ? !parentPath.equals(that.parentPath) : that.parentPath != null) return false;
        //noinspection SimplifiableIfStatement
        if (items != null ? !items.equals(that.items) : that.items != null) return false;
        return defaultItem != null ? defaultItem.equals(that.defaultItem) : that.defaultItem == null;

    }

    @Override
    public int hashCode() {
        int result = parentPath != null ? parentPath.hashCode() : 0;
        result = 31 * result + (items != null ? items.hashCode() : 0);
        result = 31 * result + (defaultItem != null ? defaultItem.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DirectoryInfo{" +
            "parentPath='" + parentPath + '\'' +
            ", items=" + items +
            ", defaultItem='" + defaultItem + '\'' +
            '}';
    }
}
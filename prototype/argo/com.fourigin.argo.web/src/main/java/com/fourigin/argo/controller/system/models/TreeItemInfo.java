package com.fourigin.argo.controller.system.models;

import com.fourigin.argo.models.structure.PageState;

public class TreeItemInfo {
    private String path;
    private String localizedName;
    private String displayName;
    private String description;
    private String templateReference;
    private PageState pageState;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplateReference() {
        return templateReference;
    }

    public void setTemplateReference(String templateReference) {
        this.templateReference = templateReference;
    }

    public PageState getPageState() {
        return pageState;
    }

    public void setPageState(PageState pageState) {
        this.pageState = pageState;
    }
}

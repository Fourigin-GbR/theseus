package com.fourigin.argo.repository.model;

import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.template.TemplateReference;

import java.util.Map;
import java.util.Objects;

public class JsonFileInfo implements JsonInfo<PageInfo> {
    private String name;
    private Map<String, String> localizedName;
    private Map<String, String> displayName;
    private String description;

    private TemplateReference templateReference;

    public JsonFileInfo() {
    }

    public JsonFileInfo(PageInfo nodeInfo){
        if(nodeInfo != null) {
            this.name = nodeInfo.getName();
            this.localizedName = nodeInfo.getLocalizedName();
            this.displayName = nodeInfo.getDisplayName();
            this.description = nodeInfo.getDescription();

            this.templateReference = nodeInfo.getTemplateReference();
        }
    }

    @Override
    public PageInfo buildNodeInfo() {
        return new PageInfo.Builder()
            .withName(name)
            .withLocalizedName(localizedName)
            .withDisplayName(displayName)
            .withDescription(description)
            .withTemplateReference(templateReference)
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

    public TemplateReference getTemplateReference() {
        return templateReference;
    }

    public void setTemplateReference(TemplateReference templateReference) {
        this.templateReference = templateReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonFileInfo)) return false;
        JsonFileInfo that = (JsonFileInfo) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(localizedName, that.localizedName) &&
            Objects.equals(displayName, that.displayName) &&
            Objects.equals(description, that.description) &&
            Objects.equals(templateReference, that.templateReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, localizedName, displayName, description, templateReference);
    }

    @Override
    public String toString() {
        return "JsonFileInfo{" +
            "name='" + name + '\'' +
            ", localizedName='" + localizedName + '\'' +
            ", displayName='" + displayName + '\'' +
            ", description='" + description + '\'' +
            ", templateReference=" + templateReference +
            '}';
    }
}

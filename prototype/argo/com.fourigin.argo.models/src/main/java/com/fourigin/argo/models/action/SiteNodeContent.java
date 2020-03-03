package com.fourigin.argo.models.action;

import com.fourigin.argo.models.template.TemplateReference;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class SiteNodeContent implements Serializable {
    private static final long serialVersionUID = 4600073430347193419L;

    private SiteStructureElementType type;
    private String name;
    private Map<String, String> localizedName;
    private Map<String, String> displayName;
    private TemplateReference templateReference;

    public SiteStructureElementType getType() {
        return type;
    }

    public void setType(SiteStructureElementType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(Map<String, String> localizedName) {
        this.localizedName = localizedName;
    }

    public Map<String, String> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Map<String, String> displayName) {
        this.displayName = displayName;
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
        if (o == null || getClass() != o.getClass()) return false;
        SiteNodeContent that = (SiteNodeContent) o;
        return type == that.type &&
                Objects.equals(name, that.name) &&
                Objects.equals(localizedName, that.localizedName) &&
                Objects.equals(displayName, that.displayName) &&
                Objects.equals(templateReference, that.templateReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, localizedName, displayName, templateReference);
    }

    @Override
    public String toString() {
        return "SiteNodeContent{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", localizedName=" + localizedName +
                ", displayName=" + displayName +
                ", templateReference=" + templateReference +
                '}';
    }
}

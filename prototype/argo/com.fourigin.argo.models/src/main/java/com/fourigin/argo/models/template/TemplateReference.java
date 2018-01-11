package com.fourigin.argo.models.template;

import java.io.Serializable;

public class TemplateReference implements Serializable {
    private static final long serialVersionUID = 398167510478917719L;

    private String templateId;
    private String variationId;
    private String revision;

    public TemplateReference() {
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getVariationId() {
        return variationId;
    }

    public void setVariationId(String variationId) {
        this.variationId = variationId;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemplateReference)) return false;

        TemplateReference that = (TemplateReference) o;

        if (templateId != null ? !templateId.equals(that.templateId) : that.templateId != null) return false;
        //noinspection SimplifiableIfStatement
        if (variationId != null ? !variationId.equals(that.variationId) : that.variationId != null) return false;
        return revision != null ? revision.equals(that.revision) : that.revision == null;
    }

    @Override
    public int hashCode() {
        int result = templateId != null ? templateId.hashCode() : 0;
        result = 31 * result + (variationId != null ? variationId.hashCode() : 0);
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TemplateReference{" +
            "templateId='" + templateId + '\'' +
            ", variationId='" + variationId + '\'' +
            ", revision='" + revision + '\'' +
            '}';
    }
}

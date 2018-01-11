package com.fourigin.argo.models.template;

import com.fourigin.argo.models.content.ContentPage;

import java.util.Collection;

public class Template {
    private String id;
    private String revision;
    private String description;
    private ContentPage prototype;
    private Collection<TemplateVariation> variations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ContentPage getPrototype() {
        return prototype;
    }

    public void setPrototype(ContentPage prototype) {
        this.prototype = prototype;
    }

    public Collection<TemplateVariation> getVariations() {
        return variations;
    }

    public void setVariations(Collection<TemplateVariation> variations) {
        this.variations = variations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Template)) return false;

        Template template = (Template) o;

        if (id != null ? !id.equals(template.id) : template.id != null) return false;
        if (revision != null ? !revision.equals(template.revision) : template.revision != null) return false;
        if (description != null ? !description.equals(template.description) : template.description != null)
            return false;
        //noinspection SimplifiableIfStatement
        if (prototype != null ? !prototype.equals(template.prototype) : template.prototype != null) return false;
        return variations != null ? variations.equals(template.variations) : template.variations == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (prototype != null ? prototype.hashCode() : 0);
        result = 31 * result + (variations != null ? variations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Template{" +
            "id='" + id + '\'' +
            ", revision='" + revision + '\'' +
            ", description='" + description + '\'' +
            ", prototype=" + prototype +
            ", variations=" + variations +
            '}';
    }
}

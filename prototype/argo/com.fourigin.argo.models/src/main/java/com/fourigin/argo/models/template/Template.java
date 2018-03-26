package com.fourigin.argo.models.template;

import com.fourigin.argo.models.content.ContentPagePrototype;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public class Template implements Serializable {
    private static final long serialVersionUID = 3693686668873051808L;

    private String id;
    private String revision;
    private String description;
    private ContentPagePrototype prototype;
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

    public ContentPagePrototype getPrototype() {
        return prototype;
    }

    public void setPrototype(ContentPagePrototype prototype) {
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
        return Objects.equals(id, template.id) &&
            Objects.equals(revision, template.revision) &&
            Objects.equals(description, template.description) &&
            Objects.equals(prototype, template.prototype) &&
            Objects.equals(variations, template.variations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, revision, description, prototype, variations);
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

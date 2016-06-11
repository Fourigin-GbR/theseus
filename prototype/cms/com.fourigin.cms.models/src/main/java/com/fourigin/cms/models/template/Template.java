package com.fourigin.cms.models.template;

import com.fourigin.cms.models.content.ContentPage;

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
}

package com.fourigin.cms.models.structure.nodes;

public class SitePage extends AbstractSiteNode implements SiteNode {
    private String contentPageReference;
    private String templateReference;

    public String getContentPageReference() {
        return contentPageReference;
    }

    public void setContentPageReference(String contentPageReference) {
        this.contentPageReference = contentPageReference;
    }

    public String getTemplateReference() {
        return templateReference;
    }

    public void setTemplateReference(String templateReference) {
        this.templateReference = templateReference;
    }
}

package com.fourigin.argo.repository.aggregators;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.PageState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.models.template.TemplateReference;
import com.fourigin.argo.repository.ContentRepository;
import com.fourigin.argo.repository.ContentResolver;

import java.util.Objects;

public class CmsRequestAggregation {
    private ContentRepository contentRepository;
    private PageInfo pageInfo;
    private PageState pageState;
    private TemplateReference templateReference;
    private Template template;
    private ContentPage contentPage;

    public ContentRepository getContentRepository(){
        return contentRepository;
    }

    public ContentResolver getContentResolver() {
        return contentRepository;
    }

    public void setContentRepository(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public PageState getPageState() {
        return pageState;
    }

    public void setPageState(PageState pageState) {
        this.pageState = pageState;
    }

    public TemplateReference getTemplateReference() {
        return templateReference;
    }

    public void setTemplateReference(TemplateReference templateReference) {
        this.templateReference = templateReference;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public ContentPage getContentPage() {
        return contentPage;
    }

    public void setContentPage(ContentPage contentPage) {
        this.contentPage = contentPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CmsRequestAggregation)) return false;
        CmsRequestAggregation that = (CmsRequestAggregation) o;
        return Objects.equals(contentRepository, that.contentRepository) &&
            Objects.equals(pageInfo, that.pageInfo) &&
            Objects.equals(pageState, that.pageState) &&
            Objects.equals(templateReference, that.templateReference) &&
            Objects.equals(template, that.template) &&
            Objects.equals(contentPage, that.contentPage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentRepository, pageInfo, pageState, templateReference, template, contentPage);
    }

    @Override
    public String toString() {
        return "CmsModelAggregator{" +
            "contentRepository=" + contentRepository +
            ", pageInfo=" + pageInfo +
            ", pageState=" + pageState +
            ", templateReference=" + templateReference +
            ", template=" + template +
            ", contentPage=" + contentPage +
            '}';
    }
}

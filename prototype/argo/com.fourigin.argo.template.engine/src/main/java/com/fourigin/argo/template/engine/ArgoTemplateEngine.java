package com.fourigin.argo.template.engine;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.models.template.TemplateVariation;

import java.io.OutputStream;

public interface ArgoTemplateEngine {
    String CONTENT_PAGE = "data_content";
    String PAGE_INFO = "data_page";
    String SITE_ATTRIBUTES = "data_attributes";

    ArgoTemplateEngine duplicate();

    void setProject(String project);

    void setLanguage(String language);

    void setPath(String path);

    void process(ContentPage contentPage, Template template, TemplateVariation templateVariation, ProcessingMode processingMode, OutputStream out);
}

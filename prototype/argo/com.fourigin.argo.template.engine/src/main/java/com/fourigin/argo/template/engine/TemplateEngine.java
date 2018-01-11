package com.fourigin.argo.template.engine;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.models.template.TemplateVariation;

import java.io.OutputStream;

public interface TemplateEngine {
    String CONTENT_PAGE = "data_content";
    String PAGE_INFO = "data_page";
    String SITE_ATTRIBUTES = "data_attributes";

    TemplateEngine duplicate();

    void setBase(String base);

    void process(ContentPage contentPage, Template template, TemplateVariation templateVariation, ProcessingMode processingMode, OutputStream out);
}

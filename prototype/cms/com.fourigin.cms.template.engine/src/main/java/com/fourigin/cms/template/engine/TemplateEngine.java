package com.fourigin.cms.template.engine;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.template.Template;
import com.fourigin.cms.models.template.TemplateVariation;

import java.io.OutputStream;

public interface TemplateEngine {
    String CONTENT_PAGE = "data_content";
    String PAGE_INFO = "data_page";
    String SITE_ATTRIBUTES = "data_attributes";

    TemplateEngine duplicate();

    void setBase(String base);

    void process(ContentPage contentPage, Template template, TemplateVariation templateVariation, ProcessingMode processingMode, OutputStream out);
}

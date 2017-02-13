package com.fourigin.cms.template.engine;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.models.template.Template;
import com.fourigin.cms.models.template.TemplateVariation;

import java.io.OutputStream;
import java.util.Map;

public interface TemplateEngine {
    TemplateEngine duplicate();

    void setBase(String base);

    void process(ContentPage contentPage, Template template, TemplateVariation templateVariation, ProcessingMode processingMode, OutputStream out);
}

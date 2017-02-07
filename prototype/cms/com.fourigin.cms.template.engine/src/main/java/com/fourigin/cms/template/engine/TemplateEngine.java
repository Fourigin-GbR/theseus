package com.fourigin.cms.template.engine;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.template.Template;
import com.fourigin.cms.models.template.TemplateVariation;

import java.io.OutputStream;

public interface TemplateEngine {
    void process(ContentPage contentPage, Template template, TemplateVariation templateVariation, OutputStream out);
}

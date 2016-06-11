package com.fourigin.cms.repository;

import com.fourigin.cms.models.template.Template;

public interface TemplateResolver {
    Template retrieve(String id);
}

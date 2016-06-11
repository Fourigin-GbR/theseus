package com.fourigin.cms.repository;

import com.fourigin.cms.models.template.Template;

public interface TemplateRepository extends TemplateResolver {
    void create(Template template);
    void update(Template template);
    void delete(String id);
}

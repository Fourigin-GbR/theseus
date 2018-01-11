package com.fourigin.argo.repository;

import com.fourigin.argo.models.template.Template;

public interface TemplateRepository extends TemplateResolver {
    void create(Template template);
    void update(Template template);
    void delete(String id);
}

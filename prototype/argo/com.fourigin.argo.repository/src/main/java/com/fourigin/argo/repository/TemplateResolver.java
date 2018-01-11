package com.fourigin.argo.repository;

import com.fourigin.argo.models.template.Template;

public interface TemplateResolver {
    Template retrieve(String id);
}

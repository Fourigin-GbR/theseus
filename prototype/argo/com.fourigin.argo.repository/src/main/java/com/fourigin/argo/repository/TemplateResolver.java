package com.fourigin.argo.repository;

import com.fourigin.argo.models.template.Template;

import java.util.Set;

public interface TemplateResolver {
    Set<Template> list(String projectId);
    Template retrieve(String projectId, String id);
}

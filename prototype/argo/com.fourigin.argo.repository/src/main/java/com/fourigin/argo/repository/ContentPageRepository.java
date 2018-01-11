package com.fourigin.argo.repository;

import com.fourigin.argo.models.content.ContentPage;

@Deprecated
public interface ContentPageRepository extends ContentPageResolver {
    void create(String parentPath, ContentPage contentPage);
    void update(String parentPath, ContentPage contentPage);
    void delete(String parentPath, String id);
}

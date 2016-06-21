package com.fourigin.cms.repository;

import com.fourigin.cms.models.content.ContentPage;

public interface ContentPageRepository extends ContentPageResolver {
    void create(String parentPath, ContentPage contentPage);
    void update(String parentPath, ContentPage contentPage);
    void delete(String parentPath, String id);
}

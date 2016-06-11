package com.fourigin.cms.repository;

import com.fourigin.cms.models.content.ContentPage;

public interface ContentPageRepository extends ContentPageResolver {
    void create(ContentPage contentPage);
    void update(ContentPage contentPage);
    void delete(String id);
}

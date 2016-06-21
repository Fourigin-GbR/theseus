package com.fourigin.cms.repository;

import com.fourigin.cms.models.content.ContentPage;

public interface ContentPageResolver {
    ContentPage retrieve(String parentPath, String id);
}
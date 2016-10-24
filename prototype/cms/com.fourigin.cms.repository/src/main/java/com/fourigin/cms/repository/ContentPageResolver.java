package com.fourigin.cms.repository;

import com.fourigin.cms.models.content.ContentPage;

@Deprecated
public interface ContentPageResolver {
    ContentPage retrieve(String parentPath, String id);
}
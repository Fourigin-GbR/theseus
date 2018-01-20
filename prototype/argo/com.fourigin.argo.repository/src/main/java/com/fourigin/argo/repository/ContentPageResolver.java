package com.fourigin.argo.repository;

import com.fourigin.argo.models.content.ContentPage;

@Deprecated
public interface ContentPageResolver {
    ContentPage retrieve(String parentPath, String id);
}

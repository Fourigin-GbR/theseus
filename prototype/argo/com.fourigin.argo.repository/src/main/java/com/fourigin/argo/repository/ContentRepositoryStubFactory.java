package com.fourigin.argo.repository;

import java.util.Map;

public class ContentRepositoryStubFactory implements ContentRepositoryFactory {
    private Map<String, ContentRepository> repositories;

    public ContentRepositoryStubFactory(Map<String, ContentRepository> repositories) {
        this.repositories = repositories;
    }

    @Override
    public ContentRepository getInstance(String key) {
        return repositories.get(key);
    }
}

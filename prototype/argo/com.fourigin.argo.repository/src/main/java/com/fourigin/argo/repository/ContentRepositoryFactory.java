package com.fourigin.argo.repository;

public interface ContentRepositoryFactory {
    ContentRepository getInstance(String key);
}

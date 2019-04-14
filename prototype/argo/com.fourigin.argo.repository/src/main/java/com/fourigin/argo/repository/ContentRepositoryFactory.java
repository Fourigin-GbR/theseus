package com.fourigin.argo.repository;

public interface ContentRepositoryFactory {
    ContentRepository getInstance(String project, String language);
}

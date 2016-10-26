package com.fourigin.cms.repository;

public interface ContentRepositoryFactory {
    ContentRepository getInstance(String key);
}

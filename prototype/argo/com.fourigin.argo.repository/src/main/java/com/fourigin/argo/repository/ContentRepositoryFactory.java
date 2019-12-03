package com.fourigin.argo.repository;

public interface ContentRepositoryFactory {
    ContentRepository getInstance(String project, String language);

    ContentRepository getInstanceForTransaction(String project, String language);

    void commitChanges(String project, String language, ContentRepository repo);

    void rollbackChanges(String project, String language, ContentRepository repo);
}

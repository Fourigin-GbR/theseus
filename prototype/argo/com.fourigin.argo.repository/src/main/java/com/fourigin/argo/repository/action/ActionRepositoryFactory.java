package com.fourigin.argo.repository.action;

public interface ActionRepositoryFactory {
    ActionRepository getInstance(String project);
}

package com.fourigin.argo.repository;

public interface RuntimeConfigurationResolverFactory {
    RuntimeConfigurationResolver getInstance(String key);
}

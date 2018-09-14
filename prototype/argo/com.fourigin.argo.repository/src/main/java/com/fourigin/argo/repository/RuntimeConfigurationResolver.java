package com.fourigin.argo.repository;

import com.fourigin.argo.models.content.config.RuntimeConfiguration;

import java.util.Set;

public interface RuntimeConfigurationResolver {
    Set<String> listAvailableConfigurations();
    RuntimeConfiguration resolveConfiguration(String name);
}

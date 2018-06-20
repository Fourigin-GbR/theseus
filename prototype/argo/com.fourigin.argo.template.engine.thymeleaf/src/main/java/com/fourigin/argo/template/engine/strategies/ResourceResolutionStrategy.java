package com.fourigin.argo.template.engine.strategies;

public interface ResourceResolutionStrategy {
    ResourceDescriptor resolveResource(String resourceId);
}

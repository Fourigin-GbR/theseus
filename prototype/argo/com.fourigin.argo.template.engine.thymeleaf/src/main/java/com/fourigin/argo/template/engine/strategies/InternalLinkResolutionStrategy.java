package com.fourigin.argo.template.engine.strategies;

public interface InternalLinkResolutionStrategy {
    String resolveLink(String customer, String base, String nodePath);
}

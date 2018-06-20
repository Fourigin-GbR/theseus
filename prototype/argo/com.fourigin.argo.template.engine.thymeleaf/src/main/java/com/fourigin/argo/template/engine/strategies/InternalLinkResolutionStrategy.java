package com.fourigin.argo.template.engine.strategies;

public interface InternalLinkResolutionStrategy {
    String resolveLink(String nodePath, String compilerBase);
}

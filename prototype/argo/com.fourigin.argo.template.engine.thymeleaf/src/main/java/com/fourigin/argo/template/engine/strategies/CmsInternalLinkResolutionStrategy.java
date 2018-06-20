package com.fourigin.argo.template.engine.strategies;

public class CmsInternalLinkResolutionStrategy implements InternalLinkResolutionStrategy {
    @Override
    public String resolveLink(String nodePath, String compilerBase) {
        return "base=" + compilerBase + "&path=" + nodePath;
    }
}

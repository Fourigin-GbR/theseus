package com.fourigin.argo.strategies;

import com.fourigin.argo.template.engine.strategies.InternalLinkResolutionStrategy;

public class CmsInternalLinkResolutionStrategy implements InternalLinkResolutionStrategy {
    @Override
    public String resolveLink(String project, String language, String nodePath) {
        return "language=" + language + "&path=" + nodePath;
    }
}

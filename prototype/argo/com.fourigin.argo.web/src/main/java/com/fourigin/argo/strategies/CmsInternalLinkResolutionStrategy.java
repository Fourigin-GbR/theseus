package com.fourigin.argo.strategies;

import com.fourigin.argo.template.engine.strategies.InternalLinkResolutionStrategy;

public class CmsInternalLinkResolutionStrategy implements InternalLinkResolutionStrategy {
    @Override
    public String resolveLink(String customer, String base, String nodePath) {
        return "base=" + base + "&path=" + nodePath;
    }
}

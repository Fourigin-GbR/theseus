package com.fourigin.argo.template.engine.utilities;

import com.fourigin.argo.template.engine.strategies.InternalLinkResolutionStrategy;

public class PagePropertiesUtilityFactory implements ThymeleafTemplateUtilityFactory<PagePropertiesUtility> {
    private InternalLinkResolutionStrategy internalLinkResolutionStrategy;

    public PagePropertiesUtilityFactory(InternalLinkResolutionStrategy internalLinkResolutionStrategy) {
        this.internalLinkResolutionStrategy = internalLinkResolutionStrategy;
    }

    @Override
    public PagePropertiesUtility getInstance() {
        PagePropertiesUtility utility = new PagePropertiesUtility();
        utility.setInternalLinkResolutionStrategy(internalLinkResolutionStrategy);
        return utility;
    }
}

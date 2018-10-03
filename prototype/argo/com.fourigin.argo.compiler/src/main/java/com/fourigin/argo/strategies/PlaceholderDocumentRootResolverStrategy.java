package com.fourigin.argo.strategies;

import com.fourigin.utilities.core.PropertiesReplacement;

import java.util.Objects;

public class PlaceholderDocumentRootResolverStrategy implements DocumentRootResolverStrategy {
    private String basePath;

    private PropertiesReplacement propertiesReplacement = new PropertiesReplacement("\\[(.+?)\\]");

    public PlaceholderDocumentRootResolverStrategy(String basePath) {
        Objects.requireNonNull(basePath, "basePath must not be null!");

        this.basePath = basePath;
    }

    @Override
    public String resolveDocumentRoot(String customer, String base) {
        return propertiesReplacement.process(basePath, "base", base, "customer", customer);
    }
}

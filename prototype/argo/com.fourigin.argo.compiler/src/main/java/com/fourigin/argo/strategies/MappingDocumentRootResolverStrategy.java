package com.fourigin.argo.strategies;

import java.util.Map;
import java.util.Objects;

public class MappingDocumentRootResolverStrategy implements DocumentRootResolverStrategy {
    private Map<String, String> mapping;

    public MappingDocumentRootResolverStrategy(Map<String, String> mapping) {
        Objects.requireNonNull(mapping, "mapping must not be null!");
        if(mapping.isEmpty()){
            throw new IllegalArgumentException("mapping must not be empty!");
        }

        this.mapping = mapping;
    }

    @Override
    public String resolveDocumentRoot(String base) {
        return mapping.get(base);
    }
}

package com.fourigin.argo.strategies;

import com.fourigin.argo.config.CustomerSpecificConfiguration;

import java.util.Map;
import java.util.Objects;

public class MappingDocumentRootResolverStrategy implements DocumentRootResolverStrategy {
    private CustomerSpecificConfiguration customerSpecificConfiguration;

    public MappingDocumentRootResolverStrategy(CustomerSpecificConfiguration customerSpecificConfiguration) {
        this.customerSpecificConfiguration = customerSpecificConfiguration;
    }

    @Override
    public String resolveDocumentRoot(String customer, String base) {
        Map<String, Map<String, String>> docRoots = customerSpecificConfiguration.getDocumentRoots();

        Map<String, String> customerRoots = docRoots.get(customer);
        Objects.requireNonNull(customerRoots, "No customer specific document root specified for '" + customer + "'!");

        return customerRoots.get(base);
    }
}

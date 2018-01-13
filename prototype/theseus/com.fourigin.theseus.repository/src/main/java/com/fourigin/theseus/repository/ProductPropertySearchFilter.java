package com.fourigin.theseus.repository;

import java.util.Set;

public class ProductPropertySearchFilter {
    private String searchKey;
    private Set<String> propertyTypes;
    private Set<String> productCodes;

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public Set<String> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(Set<String> propertyTypes) {
        this.propertyTypes = propertyTypes;
    }

    public Set<String> getProductCodes() {
        return productCodes;
    }

    public void setProductCodes(Set<String> productCodes) {
        this.productCodes = productCodes;
    }
}

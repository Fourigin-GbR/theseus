package com.fourigin.theseus.repository.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ProductSearchFilter {
    private String searchKey;
    private Set<String> productTypes;

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public Set<String> getProductTypes() {
        return productTypes;
    }

    public void setProductTypes(Set<String> productTypes) {
        this.productTypes = productTypes;
    }

    public static ProductSearchFilter forSearchKey(String key){
        return new Builder()
            .withSearchKey(key)
            .build();
    }

    public static ProductSearchFilter forProductTypes(String... types){
        return new Builder()
            .withTypes(Arrays.asList(types))
            .build();
    }

    public static ProductSearchFilter forSearchKeyAndProductTypes(String key, String... types){
        return new Builder()
            .withSearchKey(key)
            .withTypes(Arrays.asList(types))
            .build();
    }

    static class Builder {
        private String searchKey;
        private Set<String> productTypes;

        public Builder withSearchKey(String searchKey){
            this.searchKey = searchKey;

            return this;
        }

        public Builder withType(String type){
            if(productTypes == null){
                productTypes = new HashSet<>();
            }
            productTypes.add(type);

            return this;
        }

        public Builder withTypes(Collection<String> types){
            this.productTypes = new HashSet<>(types);

            return this;
        }

        public ProductSearchFilter build(){
            ProductSearchFilter result = new ProductSearchFilter();
            result.searchKey = searchKey;
            if(productTypes != null && !productTypes.isEmpty()) {
                result.productTypes = new HashSet<>(productTypes);
            }

            return result;
        }
    }
}

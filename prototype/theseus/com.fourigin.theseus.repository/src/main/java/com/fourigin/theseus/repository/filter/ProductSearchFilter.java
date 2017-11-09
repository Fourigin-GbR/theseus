package com.fourigin.theseus.repository.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ProductSearchFilter {
    private String searchKey;
    private Set<String> productTypes;

    public static ProductSearchFilter forSearchKey(String key){
        return new Builder()
            .searchKey(key)
            .build();
    }

    public static ProductSearchFilter forProductTypes(String... types){
        return new Builder()
            .types(Arrays.asList(types))
            .build();
    }

    public static ProductSearchFilter forSearchKeyAndProductTypes(String key, String... types){
        return new Builder()
            .searchKey(key)
            .types(Arrays.asList(types))
            .build();
    }

    static class Builder {
        private String searchKey;
        private Set<String> productTypes;

        public Builder searchKey(String searchKey){
            this.searchKey = searchKey;

            return this;
        }

        public Builder type(String type){
            if(productTypes == null){
                productTypes = new HashSet<>();
            }
            productTypes.add(type);

            return this;
        }

        public Builder types(Collection<String> types){
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

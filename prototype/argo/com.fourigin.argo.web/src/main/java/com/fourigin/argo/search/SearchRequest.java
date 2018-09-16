package com.fourigin.argo.search;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class SearchRequest implements Serializable {
    private static final long serialVersionUID = 6034492860487411709L;
    
    private String index;
    private Map<String, Set<String>> categories;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Map<String, Set<String>> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, Set<String>> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "SearchRequest{" +
            "index='" + index + '\'' +
            ", categories=" + categories +
            '}';
    }
}

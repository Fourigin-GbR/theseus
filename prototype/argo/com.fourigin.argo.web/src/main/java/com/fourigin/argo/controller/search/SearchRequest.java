package com.fourigin.argo.controller.search;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class SearchRequest implements Serializable {
    private static final long serialVersionUID = 6034492860487411709L;
    
    private String index;
    private Map<String, Set<String>> categories;
    private Map<String, FieldValueComparator> fields;

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

    public Map<String, FieldValueComparator> getFields() {
        return fields;
    }

    public void setFields(Map<String, FieldValueComparator> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "SearchRequest{" +
            "index='" + index + '\'' +
            ", categories=" + categories +
            ", fields=" + fields +
            '}';
    }
}

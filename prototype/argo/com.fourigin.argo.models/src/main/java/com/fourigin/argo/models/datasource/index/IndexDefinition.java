package com.fourigin.argo.models.datasource.index;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class IndexDefinition implements Serializable {
    private static final long serialVersionUID = 6246629291824599676L;
    
    private String name;
    private Map<String, String> categories;
    private Set<FieldDefinition> fields;
    private Set<String> fullTextSearch;
    private Set<String> keywords;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, String> categories) {
        this.categories = categories;
    }

    public Set<FieldDefinition> getFields() {
        return fields;
    }

    public void setFields(Set<FieldDefinition> fields) {
        this.fields = fields;
    }

    public Set<String> getFullTextSearch() {
        return fullTextSearch;
    }

    public void setFullTextSearch(Set<String> fullTextSearch) {
        this.fullTextSearch = fullTextSearch;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexDefinition)) return false;
        IndexDefinition that = (IndexDefinition) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(categories, that.categories) &&
            Objects.equals(fields, that.fields) &&
            Objects.equals(fullTextSearch, that.fullTextSearch) &&
            Objects.equals(keywords, that.keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, categories, fields, fullTextSearch, keywords);
    }

    @Override
    public String toString() {
        return "IndexDefinition{" +
            "name='" + name + '\'' +
            ", categories=" + categories +
            ", fields=" + fields +
            ", fullTextSearch=" + fullTextSearch +
            ", keywords=" + keywords +
            '}';
    }
}

package com.fourigin.argo.models.datasource.index;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DataSourceIndex implements Serializable {
    private static final long serialVersionUID = 6264288619972132472L;

    private String name;
    private Map<String, String> categories;
    private Set<FieldValue> fields;
    private Set<String> searchValues;

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

    public Set<FieldValue> getFields() {
        return fields;
    }

    public void setFields(Set<FieldValue> fields) {
        this.fields = fields;
    }

    public Set<String> getSearchValues() {
        return searchValues;
    }

    public void setSearchValues(Set<String> searchValues) {
        this.searchValues = searchValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSourceIndex)) return false;
        DataSourceIndex that = (DataSourceIndex) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(categories, that.categories) &&
            Objects.equals(fields, that.fields) &&
            Objects.equals(searchValues, that.searchValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, categories, fields, searchValues);
    }

    @Override
    public String toString() {
        return "DataSourceIndex{" +
            "name='" + name + '\'' +
            ", categories=" + categories +
            ", fields=" + fields +
            ", searchValues=" + searchValues +
            '}';
    }
}

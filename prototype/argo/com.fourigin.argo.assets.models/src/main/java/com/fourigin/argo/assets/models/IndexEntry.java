package com.fourigin.argo.assets.models;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class IndexEntry implements Serializable {
    private static final long serialVersionUID = 2372617338490494611L;

    private String key;
    private Map<String, Set<String>> values;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, Set<String>> getValues() {
        return values;
    }

    public void setValues(Map<String, Set<String>> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexEntry)) return false;
        IndexEntry that = (IndexEntry) o;
        return Objects.equals(key, that.key) &&
            Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, values);
    }

    @Override
    public String toString() {
        return "IndexEntry{" +
            "key='" + key + '\'' +
            ", values=" + values +
            '}';
    }
}

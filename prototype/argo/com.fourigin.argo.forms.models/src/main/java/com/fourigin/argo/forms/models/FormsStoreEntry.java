package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class FormsStoreEntry implements Serializable {

    private static final long serialVersionUID = 6814237595722336910L;

    private String id;

    private Map<String, String> data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormsStoreEntry)) return false;
        FormsStoreEntry that = (FormsStoreEntry) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data);
    }

    @Override
    public String toString() {
        return "FormsStoreEntry{" +
            "id='" + id + '\'' +
            ", data=" + data +
            '}';
    }
}

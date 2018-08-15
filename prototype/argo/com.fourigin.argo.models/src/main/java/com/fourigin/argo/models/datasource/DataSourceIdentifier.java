package com.fourigin.argo.models.datasource;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class DataSourceIdentifier implements Serializable, Comparable<DataSourceIdentifier> {

    private static final long serialVersionUID = 4193612352125682106L;

    private String type;
    private Map<String, String> revisions;
    private Map<String, Object> query;
    private String checksum;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Map<String, String> getRevisions() {
        return revisions;
    }

    public void setRevisions(Map<String, String> revisions) {
        this.revisions = revisions;
    }

    @Override
    public int compareTo(DataSourceIdentifier o) {
        return checksum.compareTo(o.checksum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSourceIdentifier)) return false;
        DataSourceIdentifier that = (DataSourceIdentifier) o;
        return Objects.equals(type, that.type) &&
            Objects.equals(revisions, that.revisions) &&
            Objects.equals(query, that.query) &&
            Objects.equals(checksum, that.checksum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, revisions, query, checksum);
    }

    @Override
    public String toString() {
        return "DataSourceIdentifier{" +
            "type='" + type + '\'' +
            ", revisions=" + revisions +
            ", query=" + query +
            ", checksum='" + checksum + '\'' +
            '}';
    }
}

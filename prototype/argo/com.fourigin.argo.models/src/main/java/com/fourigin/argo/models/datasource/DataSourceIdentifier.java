package com.fourigin.argo.models.datasource;

import com.fourigin.argo.models.datasource.index.IndexDefinition;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataSourceIdentifier implements Serializable, Comparable<DataSourceIdentifier> {

    private static final long serialVersionUID = 4193612352125682106L;

    private String type;
    private Map<String, String> revisions;
    private Map<String, Object> query;
    private IndexDefinition index;
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

    public IndexDefinition getIndex() {
        return index;
    }

    public void setIndex(IndexDefinition index) {
        this.index = index;
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
            Objects.equals(index, that.index) &&
            Objects.equals(checksum, that.checksum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, revisions, query, index, checksum);
    }

    @Override
    public String toString() {
        return "DataSourceIdentifier{" +
            "type='" + type + '\'' +
            ", revisions=" + revisions +
            ", query=" + query +
            ", index=" + index +
            ", checksum='" + checksum + '\'' +
            '}';
    }

    public static class Builder {
        private String type;
        private Map<String, String> revisions;
        private Map<String, Object> query;
        private IndexDefinition index;
        private String checksum;

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withRevisions(Map<String, String> revisions) {
            this.revisions = revisions;
            return this;
        }

        public Builder withQuery(Map<String, Object> query) {
            this.query = query;
            return this;
        }

        public Builder withQueryProperty(String key, Object value) {
            Objects.requireNonNull(key, "key must not be null!");

            if (query == null) {
                query = new HashMap<>();
            }

            query.put(key, value);
            return this;
        }

        public Builder withIndex(IndexDefinition index) {
            this.index = index;
            return this;
        }

        public Builder withChecksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        public DataSourceIdentifier build() {
            DataSourceIdentifier result = new DataSourceIdentifier();

            Objects.requireNonNull(type, "type must not be null!");

            result.setType(type);

            if (query != null) {
                result.setQuery(query);
            }

            if (revisions != null) {
                result.setRevisions(revisions);
            }

            if (index != null) {
                result.setIndex(index);
            }

            if (checksum != null) {
                result.setChecksum(checksum);
            }

            return result;
        }
    }
}

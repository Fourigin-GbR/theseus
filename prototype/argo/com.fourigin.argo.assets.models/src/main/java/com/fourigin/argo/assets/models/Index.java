package com.fourigin.argo.assets.models;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Index implements Serializable {
    private static final long serialVersionUID = 8327062843551702590L;

    private long createdTimestamp;
    private long lastUpdateTimestamp;
    private Map<String, IndexEntry> entries;

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public Map<String, IndexEntry> getEntries() {
        return entries;
    }

    public void setEntries(Map<String, IndexEntry> entries) {
        this.entries = entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Index)) return false;
        Index index = (Index) o;
        return createdTimestamp == index.createdTimestamp &&
            lastUpdateTimestamp == index.lastUpdateTimestamp &&
            Objects.equals(entries, index.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdTimestamp, lastUpdateTimestamp, entries);
    }

    @Override
    public String toString() {
        return "Index{" +
            "createdTimestamp=" + createdTimestamp +
            ", lastUpdateTimestamp=" + lastUpdateTimestamp +
            ", entries=" + entries +
            '}';
    }
}

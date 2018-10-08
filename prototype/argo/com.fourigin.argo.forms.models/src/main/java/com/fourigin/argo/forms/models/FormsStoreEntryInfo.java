package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class FormsStoreEntryInfo implements Serializable {
    private static final long serialVersionUID = 899512210680454843L;

    private String id;

    private long revision;

    private FormsEntryHeader header;

    private Map<String, FormsDataProcessingState> processingStates;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    public FormsEntryHeader getHeader() {
        return header;
    }

    public void setHeader(FormsEntryHeader header) {
        this.header = header;
    }

    public Map<String, FormsDataProcessingState> getProcessingStates() {
        return processingStates;
    }

    public void setProcessingStates(Map<String, FormsDataProcessingState> states) {
        this.processingStates = states;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormsStoreEntryInfo)) return false;
        FormsStoreEntryInfo that = (FormsStoreEntryInfo) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(revision, that.revision) &&
            Objects.equals(header, that.header) &&
            Objects.equals(processingStates, that.processingStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, revision, header, processingStates);
    }

    @Override
    public String toString() {
        return "FormsStoreEntryInfo{" +
            "id='" + id + '\'' +
            ", revision='" + revision + '\'' +
            ", header=" + header +
            ", processingStates=" + processingStates +
            '}';
    }
}

package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.Objects;

public class FormsStoreEntryInfo implements Serializable {
    private static final long serialVersionUID = 899512210680454843L;

    private String id;

    private long revision;

    private long creationTimestamp;

    private FormsEntryHeader header;

    private FormsDataProcessingState processingState;

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

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public FormsEntryHeader getHeader() {
        return header;
    }

    public void setHeader(FormsEntryHeader header) {
        this.header = header;
    }

    public FormsDataProcessingState getProcessingState() {
        return processingState;
    }

    public void setProcessingState(FormsDataProcessingState state) {
        this.processingState = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormsStoreEntryInfo)) return false;
        FormsStoreEntryInfo that = (FormsStoreEntryInfo) o;
        return revision == that.revision &&
            creationTimestamp == that.creationTimestamp &&
            Objects.equals(id, that.id) &&
            Objects.equals(header, that.header) &&
            Objects.equals(processingState, that.processingState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, revision, creationTimestamp, header, processingState);
    }

    @Override
    public String toString() {
        return "FormsStoreEntryInfo{" +
            "id='" + id + '\'' +
            ", revision=" + revision +
            ", creationTimestamp=" + creationTimestamp +
            ", header=" + header +
            ", processingState=" + processingState +
            '}';
    }
}

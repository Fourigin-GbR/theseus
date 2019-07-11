package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FormsStoreEntryInfo implements Serializable {
    private static final long serialVersionUID = 899512210680454843L;

    private String id;

    private int revision;

    private List<String> dataVersions;

    private String stage;

    private long creationTimestamp;

    private FormsEntryHeader header;

    private FormsDataProcessingRecord processingRecord;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public List<String> getDataVersions() {
        return dataVersions;
    }

    public void setDataVersions(List<String> dataVersions) {
        this.dataVersions = dataVersions;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
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

    public FormsDataProcessingRecord getProcessingRecord() {
        return processingRecord;
    }

    public void setProcessingRecord(FormsDataProcessingRecord state) {
        this.processingRecord = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormsStoreEntryInfo that = (FormsStoreEntryInfo) o;
        return revision == that.revision &&
                creationTimestamp == that.creationTimestamp &&
                Objects.equals(id, that.id) &&
                Objects.equals(dataVersions, that.dataVersions) &&
                Objects.equals(stage, that.stage) &&
                Objects.equals(header, that.header) &&
                Objects.equals(processingRecord, that.processingRecord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, revision, dataVersions, stage, creationTimestamp, header, processingRecord);
    }

    @Override
    public String toString() {
        return "FormsStoreEntryInfo{" +
                "id='" + id + '\'' +
                ", revision=" + revision +
                ", dataVersions=" + dataVersions +
                ", stage='" + stage + '\'' +
                ", creationTimestamp=" + creationTimestamp +
                ", header=" + header +
                ", processingRecord=" + processingRecord +
                '}';
    }
}

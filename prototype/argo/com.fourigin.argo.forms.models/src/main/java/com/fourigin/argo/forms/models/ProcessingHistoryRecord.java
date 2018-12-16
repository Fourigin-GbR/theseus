package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.Objects;

public class ProcessingHistoryRecord implements Serializable {
    private static final long serialVersionUID = -4844436078035132403L;

    private String key;

    private String value;

    private long timestamp;

    public static final String KEY_MESSAGE = "status/message";
    public static final String KEY_STATUS_CHANGE = "status/change";
    public static final String KEY_PROCESSING_START = "processing/start";
    public static final String KEY_PROCESSING_DONE = "processing/done";
    public static final String KEYPREFIX_PROCESSING = "processing/";

    public ProcessingHistoryRecord() {
    }

    public ProcessingHistoryRecord(String key) {
        this(key, null);
    }

    public ProcessingHistoryRecord(String key, String value) {
        this.key = key;
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessingHistoryRecord)) return false;
        ProcessingHistoryRecord that = (ProcessingHistoryRecord) o;
        return timestamp == that.timestamp &&
            Objects.equals(key, that.key) &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, timestamp);
    }

    @Override
    public String toString() {
        return "ProcessingHistoryRecord{" +
            "key='" + key + '\'' +
            ", value='" + value + '\'' +
            ", timestamp=" + timestamp +
            '}';
    }
}

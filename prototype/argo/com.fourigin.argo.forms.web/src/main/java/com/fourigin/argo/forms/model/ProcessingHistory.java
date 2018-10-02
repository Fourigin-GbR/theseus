package com.fourigin.argo.forms.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class ProcessingHistory implements Serializable {
    private static final long serialVersionUID = -4844436078035132403L;

    private long timestamp;

    private ProcessingState state;

    private Map<String, String> context;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ProcessingState getState() {
        return state;
    }

    public void setState(ProcessingState state) {
        this.state = state;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessingHistory)) return false;
        ProcessingHistory that = (ProcessingHistory) o;
        return timestamp == that.timestamp &&
            state == that.state &&
            Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, state, context);
    }

    @Override
    public String toString() {
        return "ProcessingHistory{" +
            "timestamp=" + timestamp +
            ", state=" + state +
            ", context=" + context +
            '}';
    }
}

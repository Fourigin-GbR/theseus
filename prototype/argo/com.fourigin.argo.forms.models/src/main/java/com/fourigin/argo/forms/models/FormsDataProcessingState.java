package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FormsDataProcessingState implements Serializable {
    private static final long serialVersionUID = -9151906834376089744L;

    private ProcessingState processingState;

    private List<ProcessingHistoryRecord> processingHistory;

    public ProcessingState getProcessingState() {
        return processingState;
    }

    public void setProcessingState(ProcessingState processingState) {
        this.processingState = processingState;
    }

    public List<ProcessingHistoryRecord> getProcessingHistory() {
        return processingHistory;
    }

    public void setProcessingHistory(List<ProcessingHistoryRecord> processingHistory) {
        this.processingHistory = processingHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormsDataProcessingState)) return false;
        FormsDataProcessingState that = (FormsDataProcessingState) o;
        return processingState == that.processingState &&
            Objects.equals(processingHistory, that.processingHistory);
    }

    @Override
    public int hashCode() {

        return Objects.hash(processingState, processingHistory);
    }

    @Override
    public String toString() {
        return "FormsDataProcessingState{" +
            "processingState=" + processingState +
            ", processingHistory=" + processingHistory +
            '}';
    }
}

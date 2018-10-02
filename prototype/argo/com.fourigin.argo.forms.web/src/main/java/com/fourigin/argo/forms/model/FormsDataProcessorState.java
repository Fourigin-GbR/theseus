package com.fourigin.argo.forms.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FormsDataProcessorState implements Serializable {
    private static final long serialVersionUID = -9151906834376089744L;

    private String processor;

    private ProcessingState state;

    private List<ProcessingHistory> processingHistory;

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public ProcessingState getState() {
        return state;
    }

    public void setState(ProcessingState state) {
        this.state = state;
    }

    public List<ProcessingHistory> getProcessingHistory() {
        return processingHistory;
    }

    public void setProcessingHistory(List<ProcessingHistory> processingHistory) {
        this.processingHistory = processingHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormsDataProcessorState)) return false;
        FormsDataProcessorState that = (FormsDataProcessorState) o;
        return Objects.equals(processor, that.processor) &&
            state == that.state &&
            Objects.equals(processingHistory, that.processingHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processor, state, processingHistory);
    }

    @Override
    public String toString() {
        return "FormsDataProcessorState{" +
            "processor='" + processor + '\'' +
            ", state=" + state +
            ", processingHistory=" + processingHistory +
            '}';
    }
}

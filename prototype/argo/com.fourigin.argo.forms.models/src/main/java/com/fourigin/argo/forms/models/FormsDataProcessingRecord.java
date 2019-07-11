package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FormsDataProcessingRecord implements Serializable {
    private static final long serialVersionUID = -9151906834376089744L;

    private ProcessingState state;

    private String currentStatusMessage;

    private List<ProcessingHistoryRecord> history;

    public ProcessingState getState() {
        return state;
    }
    
    public void setState(ProcessingState state) {
        addHistoryRecord(ProcessingHistoryRecord.KEY_STATUS_CHANGE, state.name());

        this.state = state;
    }

    public String getCurrentStatusMessage() {
        return currentStatusMessage;
    }

    public void setCurrentStatusMessage(String currentStatusMessage) {
        this.currentStatusMessage = currentStatusMessage;
    }

    public List<ProcessingHistoryRecord> getHistory() {
        return history;
    }

    public void setHistory(List<ProcessingHistoryRecord> history) {
        this.history = history;
    }

    public void addHistoryRecord(ProcessingHistoryRecord historyRecord){
        Objects.requireNonNull(historyRecord, "historyRecord must not be null!");

        if(history == null){
            history = new ArrayList<>();
        }

        history.add(historyRecord);
    }

    public void addHistoryRecord(String key){
        addHistoryRecord(new ProcessingHistoryRecord(key));
    }

    public void addHistoryRecord(String key, String value){
        addHistoryRecord(new ProcessingHistoryRecord(key, value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormsDataProcessingRecord)) return false;
        FormsDataProcessingRecord that = (FormsDataProcessingRecord) o;
        return state == that.state &&
            Objects.equals(currentStatusMessage, that.currentStatusMessage) &&
            Objects.equals(history, that.history);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, currentStatusMessage, history);
    }

    @Override
    public String toString() {
        return "FormsDataProcessingRecord{" +
            "state=" + state +
            ", currentStatusMessage='" + currentStatusMessage + '\'' +
            ", history=" + history +
            '}';
    }
}

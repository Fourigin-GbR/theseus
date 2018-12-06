package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FormsDataProcessingState implements Serializable {
    private static final long serialVersionUID = -9151906834376089744L;

    private ProcessingState state;

    private List<ProcessingHistoryRecord> history;

    public ProcessingState getState() {
        return state;
    }

    public void setState(ProcessingState state) {
        this.state = state;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormsDataProcessingState)) return false;
        FormsDataProcessingState that = (FormsDataProcessingState) o;
        return state == that.state &&
            Objects.equals(history, that.history);
    }

    @Override
    public int hashCode() {

        return Objects.hash(state, history);
    }

    @Override
    public String toString() {
        return "FormsDataProcessingState{" +
            "state=" + state +
            ", history=" + history +
            '}';
    }
}

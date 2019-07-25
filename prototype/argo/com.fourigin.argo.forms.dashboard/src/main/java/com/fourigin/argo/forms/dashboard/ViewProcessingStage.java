package com.fourigin.argo.forms.dashboard;

import com.fourigin.argo.forms.models.ProcessingState;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class ViewProcessingStage implements Serializable {
    private static final long serialVersionUID = -2709594233481353553L;

    private String name;
    private boolean editable;
    private Map<String, ProcessingState> actions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public Map<String, ProcessingState> getActions() {
        return actions;
    }

    public void setActions(Map<String, ProcessingState> actions) {
        this.actions = actions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewProcessingStage that = (ViewProcessingStage) o;
        return editable == that.editable &&
                Objects.equals(name, that.name) &&
                Objects.equals(actions, that.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, editable, actions);
    }

    @Override
    public String toString() {
        return "ViewProcessingStage{" +
                "name='" + name + '\'' +
                ", editable=" + editable +
                ", actions=" + actions +
                '}';
    }
}

package com.fourigin.argo.forms.definition;

import com.fourigin.argo.forms.models.ProcessingState;
import com.fourigin.utilities.reflection.InitializableObjectDescriptor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProcessingStage implements Serializable {
    private static final long serialVersionUID = 1426472117127278702L;

    private String name;
    private List<InitializableObjectDescriptor> processors;
    private Map<String, ProcessingState> actions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<InitializableObjectDescriptor> getProcessors() {
        return processors;
    }

    public void setProcessors(List<InitializableObjectDescriptor> processors) {
        this.processors = processors;
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
        ProcessingStage that = (ProcessingStage) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(processors, that.processors) &&
                Objects.equals(actions, that.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, processors, actions);
    }

    @Override
    public String toString() {
        return "ProcessingStage{" +
                "name='" + name + '\'' +
                ", processors=" + processors +
                ", actions=" + actions +
                '}';
    }
}

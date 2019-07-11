package com.fourigin.argo.forms.definition;

import com.fourigin.utilities.reflection.InitializableObjectDescriptor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ProcessingStage implements Serializable {
    private static final long serialVersionUID = 1426472117127278702L;

    private String name;
    private List<InitializableObjectDescriptor> processors;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessingStage that = (ProcessingStage) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(processors, that.processors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, processors);
    }

    @Override
    public String toString() {
        return "ProcessingStage{" +
                "name='" + name + '\'' +
                ", processors=" + processors +
                '}';
    }
}

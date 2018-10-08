package com.fourigin.argo.forms;

import com.fourigin.argo.forms.models.ProcessingState;

import java.util.Map;

public class FormsRegistry {
    private Map<String, ProcessingState> entries;

    public Map<String, ProcessingState> getEntries() {
        return entries;
    }

    public void setEntries(Map<String, ProcessingState> entries) {
        this.entries = entries;
    }
}

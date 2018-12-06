package com.fourigin.argo.forms;

public interface FormsEntryProcessor {
    String getName();

    void processEntry(String entryId, FormsRegistry registry);
}

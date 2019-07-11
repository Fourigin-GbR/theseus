package com.fourigin.argo.forms;

public interface FormsEntryProcessor {
    String getName();

    boolean processEntry(String entryId);
}

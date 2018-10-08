package com.fourigin.argo.forms;

public interface FormsProcessingSupervisor {
    void initializeRegistry();

    void registerDispatcher(FormsProcessingDispatcher dispatcher);

    void resetFormEntryProcessing(String entryId);

    FormsRegistry getRegistry();
}

package com.fourigin.argo.forms;

import com.fourigin.argo.forms.models.ProcessingState;

public interface FormsEntryProcessor {
    String getName();

    ProcessingState processEntry(String entryId, FormsRegistry registry);
}

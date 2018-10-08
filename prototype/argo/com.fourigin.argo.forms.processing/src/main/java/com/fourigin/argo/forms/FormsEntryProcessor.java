package com.fourigin.argo.forms;

import com.fourigin.argo.forms.models.ProcessingHistoryRecord;

public interface FormsEntryProcessor {
    String getName();

    ProcessingHistoryRecord processEntry(String entryId, FormsRegistry registry);
}

package com.fourigin.argo.forms;

import com.fourigin.argo.forms.models.FormsStoreEntryInfo;

public interface FormsProcessingDispatcher {
    void processFormEntry(FormsStoreEntryInfo entryInfo);
}

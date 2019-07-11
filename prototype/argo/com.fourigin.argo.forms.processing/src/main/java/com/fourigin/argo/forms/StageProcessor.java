package com.fourigin.argo.forms;

import com.fourigin.argo.forms.definition.ProcessingStage;
import com.fourigin.argo.forms.definition.ProcessingStages;
import com.fourigin.argo.forms.models.FormsDataProcessingRecord;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;

public interface StageProcessor {
    void processEntry(
            FormsStoreEntryInfo info,
            FormsDataProcessingRecord record,
            ProcessingStage currentStage,
            ProcessingStages stages,
            FormsStoreRepository formsStoreRepository
    );
}

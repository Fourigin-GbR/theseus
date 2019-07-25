package com.fourigin.argo.forms;

import com.fourigin.argo.forms.definition.ProcessingStage;
import com.fourigin.argo.forms.definition.ProcessingStages;
import com.fourigin.argo.forms.models.FormsDataProcessingRecord;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingState;
import com.fourigin.utilities.reflection.Initializable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ChangeState implements Initializable, StageProcessor {

    private ProcessingState targetState;

    @Override
    public Collection<String> requiredKeys() {
        return Collections.singletonList("target-state");
    }

    @Override
    public void initialize(Map<String, Object> settings) {
        targetState = ProcessingState.valueOf((String) settings.get("target-state"));
    }

    @Override
    public void processEntry(FormsStoreEntryInfo info, FormsDataProcessingRecord record, ProcessingStage currentStage, ProcessingStages stages, FormsStoreRepository formsStoreRepository) {
        record.setState(targetState);
    }
}

package com.fourigin.argo.forms.processing;

import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.StageProcessor;
import com.fourigin.argo.forms.definition.ProcessingStage;
import com.fourigin.argo.forms.definition.ProcessingStages;
import com.fourigin.argo.forms.models.FormsDataProcessingRecord;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingState;
import com.fourigin.utilities.reflection.Initializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static com.fourigin.argo.forms.processing.TspFieldNames.NEW_TEMPLATE_FINAL_VALUE;

public class NewNameplateVerifier implements Initializable, StageProcessor {

    private ProcessingState nameplateAvailableState;
    private ProcessingState nameplateNotAvailableState;
    //    private ProcessingState failedState;

    @Override
    public Collection<String> requiredKeys() {
        return Arrays.asList("nameplate-available-state", "nameplate-not-available-state");
    }

    @Override
    public void initialize(Map<String, Object> settings) {
        nameplateAvailableState = ProcessingState.valueOf((String) settings.get("nameplate-available-state"));
        nameplateNotAvailableState = ProcessingState.valueOf((String) settings.get("nameplate-not-available-state"));
//        failedState = ProcessingState.valueOf((String) settings.get("fail-state"));
    }

    @Override
    public void processEntry(
            FormsStoreEntryInfo info,
            FormsDataProcessingRecord record,
            ProcessingStage currentStage,
            ProcessingStages stages,
            FormsStoreRepository formsStoreRepository
    ) {
        FormsStoreEntry entry = formsStoreRepository.retrieveEntry(info);
        Map<String, String> data = entry.getData();
        String newNameplate = data.get(NEW_TEMPLATE_FINAL_VALUE);
        if (newNameplate != null && !newNameplate.isEmpty()) {
            record.setState(nameplateAvailableState);
            return;
        }

        record.setCurrentStatusMessage("Missing value for new nameplate!");
        record.setState(nameplateNotAvailableState);
    }
}

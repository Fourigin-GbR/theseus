package com.fourigin.argo.forms.processing;

import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.StageProcessor;
import com.fourigin.argo.forms.definition.ProcessingStage;
import com.fourigin.argo.forms.definition.ProcessingStages;
import com.fourigin.argo.forms.models.FormsDataProcessingRecord;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.utilities.reflection.Initializable;

import java.util.Collection;
import java.util.Map;

import static com.fourigin.argo.forms.models.ProcessingState.WAITING_FOR_INPUT;
import static com.fourigin.argo.forms.processing.TspFieldNames.NEW_TEMPLATE_FINAL_VALUE;

public class NewNameplateVerifier implements Initializable, StageProcessor {

    @Override
    public Collection<String> requiredKeys() {
        return null;
    }

    @Override
    public void initialize(Map<String, Object> settings) {

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
            return;
        }

        record.setCurrentStatusMessage("Missing value for new nameplate!");
        record.setState(WAITING_FOR_INPUT);
    }
}

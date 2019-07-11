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

import static com.fourigin.argo.forms.models.ProcessingState.PENDING;

public class NewNameplateCarrier implements Initializable, StageProcessor {

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
        String newNameplateType = data.get("vehicle.new-nameplate");
        if ("self".equals(newNameplateType)) {
            String newNameplateValue = data.get("vehicle.new-nameplate/nameplate-customer-reservation");
            data.put("vehicle.valid-new-nameplate", newNameplateValue);

            entry.setData(data);
            formsStoreRepository.updateEntry(info, entry);

            info.setStage(stages.getNext(currentStage.getName()).getName());
            record.setState(PENDING);
        }
    }
}

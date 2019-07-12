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
import static com.fourigin.argo.forms.processing.TspFieldNames.NEW_TEMPLATE_FINAL_VALUE;
import static com.fourigin.argo.forms.processing.TspFieldNames.NEW_TEMPLATE_REGISTRATION_TYPE;
import static com.fourigin.argo.forms.processing.TspFieldNames.NEW_TEMPLATE_REGISTRATION_TYPE_CUSTOMER;
import static com.fourigin.argo.forms.processing.TspFieldNames.NEW_TEMPLATE_VALUE_RESERVED_BY_CUSTOMER;

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

        // check the existing final nameplate
        String newNameplate = data.get(NEW_TEMPLATE_FINAL_VALUE);
        if (newNameplate != null && !newNameplate.isEmpty()) {
            info.setStage(stages.getNext(currentStage.getName()).getName());
            return;
        }

        String newNameplateType = data.get(NEW_TEMPLATE_REGISTRATION_TYPE);
        if (NEW_TEMPLATE_REGISTRATION_TYPE_CUSTOMER.equals(newNameplateType)) {
            String newNameplateValue = data.get(NEW_TEMPLATE_VALUE_RESERVED_BY_CUSTOMER);
            data.put(NEW_TEMPLATE_FINAL_VALUE, newNameplateValue);

            entry.setData(data);
            formsStoreRepository.updateEntry(info, entry);

            info.setStage(stages.getNext(currentStage.getName()).getName());
            record.setState(PENDING);
        }
    }
}

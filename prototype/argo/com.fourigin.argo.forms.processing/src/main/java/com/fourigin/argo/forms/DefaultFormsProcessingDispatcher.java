package com.fourigin.argo.forms;

import com.fourigin.argo.forms.models.FormsDataProcessingState;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingHistoryRecord;
import com.fourigin.argo.forms.models.ProcessingState;

import java.util.List;

public class DefaultFormsProcessingDispatcher implements FormsProcessingDispatcher {

    private FormsStoreRepository formsStoreRepository;

    private FormsEntryProcessorMapping processorMapping;

    private FormsEntryProcessorFactory processorFactory;

    private FormsRegistry registry;

    public static final String REGISTRATION_PROCESSING_STATE = "REGISTRATION_PROCESSING_STATE";

    public DefaultFormsProcessingDispatcher(
        FormsStoreRepository formsStoreRepository,
        FormsEntryProcessorMapping processorMapping,
        FormsEntryProcessorFactory processorFactory,
        FormsRegistry registry
    ) {
        this.formsStoreRepository = formsStoreRepository;
        this.processorMapping = processorMapping;
        this.processorFactory = processorFactory;
        this.registry = registry;
    }

    @Override
    public void processFormEntry(String entryId) {
        FormsStoreEntryInfo info = formsStoreRepository.retrieveEntryInfo(entryId);
        String formDefinitionId = info.getHeader().getFormDefinition();

        boolean success = true;

        FormsDataProcessingState state = info.getProcessingState();
        if (state == null) {
            state = new FormsDataProcessingState();
        }

        state.addHistoryRecord(new ProcessingHistoryRecord("status/change", ProcessingState.PROCESSING.name()));
        state.setState(ProcessingState.PROCESSING);

        List<String> processorNames = processorMapping.get(formDefinitionId);
        if (processorNames != null && !processorNames.isEmpty()) {
            state.addHistoryRecord(new ProcessingHistoryRecord("processing/start"));

            for (String processorName : processorNames) {
                try {
                    FormsEntryProcessor processor = processorFactory.getInstance(processorName);
                    processor.processEntry(entryId, registry);
                    state.addHistoryRecord(new ProcessingHistoryRecord("processing/" + processorName));
                } catch (Throwable th) {
                    success = false;
                }
            }

            state.addHistoryRecord(new ProcessingHistoryRecord("processing/done"));
        }

        // reload updated info entry
        info = formsStoreRepository.retrieveEntryInfo(entryId);

        // set state
        if (success) {
            state.addHistoryRecord(new ProcessingHistoryRecord("status/change", ProcessingState.WAITING.name()));
            state.setState(ProcessingState.WAITING);
        } else {
            state.setState(ProcessingState.FAILED);
        }
        info.setProcessingState(state);

        // update entry
        formsStoreRepository.updateEntryInfo(info);
    }
}

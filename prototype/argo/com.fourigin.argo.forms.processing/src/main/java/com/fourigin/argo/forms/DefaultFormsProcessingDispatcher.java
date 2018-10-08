package com.fourigin.argo.forms;

import com.fourigin.argo.forms.models.FormsDataProcessingState;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingHistoryRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultFormsProcessingDispatcher implements FormsProcessingDispatcher {

    private FormsStoreRepository formsStoreRepository;

    private FormsEntryProcessorMapping processorMapping;

    private FormsEntryProcessorFactory processorFactory;

    private FormsRegistry registry;

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
    public void registerFormEntry(String entryId) {
        FormsStoreEntryInfo info = formsStoreRepository.retrieveEntryInfo(entryId);
        String formDefinitionId = info.getHeader().getFormDefinition();

        List<String> processorNames = processorMapping.get(formDefinitionId);
        if (processorNames != null && !processorNames.isEmpty()) {
            Map<String, FormsDataProcessingState> states = info.getProcessingStates();

            for (String processorName : processorNames) {
                FormsEntryProcessor processor = processorFactory.getInstance(processorName);
                ProcessingHistoryRecord historyRecord = processor.processEntry(entryId, registry);

                FormsDataProcessingState state = states.get(processorName);
                List<ProcessingHistoryRecord> history = state.getProcessingHistory();
                if (history == null) {
                    history = new ArrayList<>();
                    state.setProcessingHistory(history);
                }

                history.add(historyRecord);
            }
        }
    }
}

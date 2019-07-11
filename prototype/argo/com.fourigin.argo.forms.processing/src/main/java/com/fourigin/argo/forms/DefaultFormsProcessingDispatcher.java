package com.fourigin.argo.forms;

import com.fourigin.argo.forms.definition.ProcessingStage;
import com.fourigin.argo.forms.definition.ProcessingStages;
import com.fourigin.argo.forms.models.FormsDataProcessingRecord;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingHistoryRecord;
import com.fourigin.argo.forms.models.ProcessingState;
import com.fourigin.utilities.reflection.InitializableObjectDescriptor;
import com.fourigin.utilities.reflection.ObjectInitializer;

import java.util.List;

public class DefaultFormsProcessingDispatcher implements FormsProcessingDispatcher {

    private FormDefinitionResolver formDefinitionResolver;

    private FormsStoreRepository formsStoreRepository;

    private FormsEntryProcessorMapping processorMapping;

    private FormsEntryProcessorFactory processorFactory;

    public DefaultFormsProcessingDispatcher(
            FormDefinitionResolver formDefinitionResolver,
            FormsStoreRepository formsStoreRepository,
            FormsEntryProcessorMapping processorMapping,
            FormsEntryProcessorFactory processorFactory
    ) {
        this.formDefinitionResolver = formDefinitionResolver;
        this.formsStoreRepository = formsStoreRepository;
        this.processorMapping = processorMapping;
        this.processorFactory = processorFactory;
    }

    @Override
    public void processFormEntry(FormsStoreEntryInfo info) {
        String formDefinitionId = info.getHeader().getFormDefinition();

        String entryId = info.getId();
        String currentStageName = info.getStage();

        boolean success = true;

        FormsDataProcessingRecord record = info.getProcessingRecord();
        if (record == null) {
            record = new FormsDataProcessingRecord();
        }

        try {
            record.setState(ProcessingState.PROCESSING);

            List<String> processorNames = processorMapping.get(formDefinitionId + ":" + currentStageName);
            if (processorNames != null && !processorNames.isEmpty()) {
                record.addHistoryRecord(ProcessingHistoryRecord.KEY_PROCESSING_START);

                for (String processorName : processorNames) {
                    try {
                        FormsEntryProcessor processor = processorFactory.getInstance(processorName);
                        if (!processor.processEntry(entryId)) {
                            success = false;
                        } else {
                            record.addHistoryRecord(ProcessingHistoryRecord.PREFIX_PROCESSING + processorName);
                        }
                    } catch (Throwable th) {
                        success = false;
                    }
                }

                record.addHistoryRecord(ProcessingHistoryRecord.KEY_PROCESSING_DONE);
            }

            // reload updated info entry
            info = formsStoreRepository.retrieveEntryInfo(entryId);

            // set state
            if (success) {

                ProcessingStages stages = formDefinitionResolver.retrieveStages(formDefinitionId);

                if (stages.isLast(currentStageName)) {
                    record.setState(ProcessingState.READY_TO_APPROVE);
                } else {
                    record.setState(ProcessingState.WAITING_FOR_INPUT);

                    ProcessingStage nextStage = stages.getNext(currentStageName);
                    info.setStage(nextStage.getName());
                }

                ProcessingStage currentStage = stages.find(currentStageName);
                List<InitializableObjectDescriptor> stageProcessors = currentStage.getProcessors();
                if (stageProcessors != null && !stageProcessors.isEmpty()) {
                    for (InitializableObjectDescriptor stageProcessorDescriptor : stageProcessors) {
                        StageProcessor stageProcessor = ObjectInitializer.initialize(stageProcessorDescriptor);
                        stageProcessor.processEntry(info, record, currentStage, stages, formsStoreRepository);
                    }
                }

            } else {
                record.setState(ProcessingState.FAILED);
            }
            info.setProcessingRecord(record);

            // update entry
            formsStoreRepository.updateEntryInfo(info);
        } catch (Exception ex) {
            record.setCurrentStatusMessage("Internal error: " + ex.getMessage());
            record.setState(ProcessingState.FAILED);

            info.setProcessingRecord(record);

            formsStoreRepository.updateEntryInfo(info);
        }
    }
}

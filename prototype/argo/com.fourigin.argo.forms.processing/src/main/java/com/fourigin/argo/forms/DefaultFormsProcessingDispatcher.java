package com.fourigin.argo.forms;

import com.fourigin.argo.forms.definition.ProcessingStage;
import com.fourigin.argo.forms.definition.ProcessingStages;
import com.fourigin.argo.forms.models.FormsDataProcessingRecord;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingHistoryRecord;
import com.fourigin.argo.forms.models.ProcessingState;
import com.fourigin.utilities.reflection.InitializableObjectDescriptor;
import com.fourigin.utilities.reflection.ObjectInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DefaultFormsProcessingDispatcher implements FormsProcessingDispatcher {

    private FormDefinitionResolver formDefinitionResolver;

    private FormsStoreRepository formsStoreRepository;

    private FormsEntryProcessorMapping processorMapping;

    private FormsEntryProcessorFactory processorFactory;

    private final Logger logger = LoggerFactory.getLogger(DefaultFormsProcessingDispatcher.class);

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
                        if (logger.isErrorEnabled()) logger.error("Error executing processor {}", processorName, th);
                        record.addHistoryRecord(ProcessingHistoryRecord.PREFIX_FAILURE + processorName, th.getMessage());
                        success = false;
                    }
                }

                record.addHistoryRecord(ProcessingHistoryRecord.KEY_PROCESSING_DONE);
            }

            // reload updated info entry
            info = formsStoreRepository.retrieveEntryInfo(entryId);

            // set state
            if (!success) {
                record.setState(ProcessingState.FAILED);
            } else {
                ProcessingStages stages = formDefinitionResolver.retrieveStages(formDefinitionId);

                boolean lastStage;
                if (!stages.isLast(currentStageName)) {
                    lastStage = false;

                    // go to the next stage if possible
                    ProcessingStage nextStage = stages.getNext(currentStageName);
                    String nextStageName = nextStage.getName();
                    record.addHistoryRecord(ProcessingHistoryRecord.KEY_STAGE_CHANGE, nextStageName);
                    info.setStage(nextStageName);
                }
                else {
                    lastStage = true;
                }

                // execute current stage processors
                ProcessingStage currentStage = stages.find(currentStageName);
                List<InitializableObjectDescriptor> stageProcessors = currentStage.getProcessors();
                if (stageProcessors != null && !stageProcessors.isEmpty()) {
                    for (InitializableObjectDescriptor stageProcessorDescriptor : stageProcessors) {
                        StageProcessor stageProcessor = ObjectInitializer.initialize(stageProcessorDescriptor);
                        stageProcessor.processEntry(info, record, currentStage, stages, formsStoreRepository);
                    }
                }

                if (record.getState() == ProcessingState.PROCESSING) {
                    // change state if not already changed by any of processors
                    record.setState(lastStage ? ProcessingState.DONE : ProcessingState.PROCESSED);
                }
            }

            info.setProcessingRecord(record);

            // update entry
            formsStoreRepository.updateEntryInfo(info);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) logger.error("Error occurred!", ex);
            record.setCurrentStatusMessage("Internal error: " + ex.getMessage());
            record.setState(ProcessingState.FAILED);

            info.setProcessingRecord(record);

            formsStoreRepository.updateEntryInfo(info);
        }
    }
}

package com.fourigin.argo.forms.scheduling;

import com.fourigin.argo.forms.FormsProcessingDispatcher;
import com.fourigin.argo.forms.FormsStoreRepository;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingState;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ProcessFormRequestsJob implements Job {

    private FormsStoreRepository formsStoreRepository;

    private FormsProcessingDispatcher formsProcessingDispatcher;

    private final Logger logger = LoggerFactory.getLogger(ProcessFormRequestsJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        if (logger.isDebugEnabled()) logger.debug("Searching for pending entries ...");
        Collection<FormsStoreEntryInfo> pendingEntryInfos = formsStoreRepository.findEntryInfosByProcessingState(ProcessingState.PENDING);

        if (pendingEntryInfos == null || pendingEntryInfos.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("No pending entries found, job execution finished");
            return;
        }

        if (logger.isDebugEnabled()) logger.debug("Start processing of {} pending entries", pendingEntryInfos.size());

        for (FormsStoreEntryInfo entryInfo : pendingEntryInfos) {
            if (logger.isInfoEnabled()) logger.info("Processing entry {}", entryInfo.getId());
            formsProcessingDispatcher.processFormEntry(entryInfo);
        }

        if (logger.isDebugEnabled()) logger.debug("Processing of all entries done.");
    }

    @Autowired
    public void setFormsStoreRepository(FormsStoreRepository formsStoreRepository) {
        this.formsStoreRepository = formsStoreRepository;
    }

    @Autowired
    public void setFormsProcessingDispatcher(FormsProcessingDispatcher formsProcessingDispatcher) {
        this.formsProcessingDispatcher = formsProcessingDispatcher;
    }
}

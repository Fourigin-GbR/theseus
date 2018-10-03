package com.fourigin.argo.forms;

import com.fourigin.argo.forms.models.Attachment;
import com.fourigin.argo.forms.models.FormsStoreEntry;
import com.fourigin.argo.forms.models.FormsStoreEntryHeader;
import com.fourigin.argo.forms.models.FormsStoreEntryInfo;
import com.fourigin.argo.forms.models.ProcessingState;

import java.util.Map;
import java.util.Set;

public interface FormsStoreRepository {
    // entry
    String createEntry(FormsStoreEntry entry);
    void deleteEntry(String entryId);
    FormsStoreEntry retrieveEntry(String entryId);

    // info
    void createEntryInfo(String entryId, FormsStoreEntryHeader header);
    FormsStoreEntryInfo retrieveEntryInfo(String entryId);

    // processing states
    void addProcessingState(String entryId, String processorName, ProcessingState state);
    void addProcessingState(String entryId, String processorName, ProcessingState state, Map<String, String> context);

    // attachments
    Set<String> getAttachmentNames(String entryId);
    void addAttachment(String entryId, String name, Attachment attachment);
    Attachment getAttachment(String entryId, String name);
    void deleteAttachment(String entryId, String name);
}

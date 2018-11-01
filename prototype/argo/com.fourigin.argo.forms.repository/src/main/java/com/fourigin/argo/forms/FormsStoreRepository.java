package com.fourigin.argo.forms;

import com.fourigin.argo.forms.models.FormsEntryHeader;
import com.fourigin.argo.forms.models.FormsStoreEntry;
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
    void createEntryInfo(String entryId, FormsEntryHeader header);
    FormsStoreEntryInfo retrieveEntryInfo(String entryId);
    void updateEntryInfo(FormsStoreEntryInfo info);

    // processing states
    void addProcessingState(String entryId, String processorName, ProcessingState state);
    void addProcessingState(String entryId, String processorName, ProcessingState state, Map<String, String> context);

    // attachments
    Set<AttachmentDescriptor> getAttachmentDescriptors(String entryId);

    void addObjectAttachment(String entryId, String name, Object attachment);
    void addBinaryAttachment(String entryId, String name, String mimeType, byte[] data);

    <T> T getObjectAttachment(String entryId, String name, Class<T> target);
    byte[] getBinaryAttachment(String entryId, String name, String mimeType);

    void deleteAttachment(String entryId, String name);
}

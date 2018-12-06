package com.fourigin.argo.forms.models;

public enum ProcessingState {
    FAILED, // processing failed, manual action required
    PENDING, // stored, waiting for processing
    PROCESSING, // processing
    SUSPENDED, // processing suspended, waiting for automatically retry
    REJECTED, // processing rejected because of some external requirements, manual action required
    WAITING, // waiting for external processes (offline), manual action required
    DONE // processing finished
}

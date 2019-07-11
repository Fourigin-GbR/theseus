package com.fourigin.argo.forms.models;

/*
    1.
        All new requests are PENDING. Only PENDING requests will be processed.
    2.
        While processing the request state is PROCESSING.
    3.
        In case of failure the request state changes to FAILED.
        In case of successful processing the request's form definition will be checked for multiple stages.
        a)
            If the request processing is in the last defined stage, the request state changes to READY_TO_APPROVAL.
        b)
            If the request processing is not in the last defined stage, the request state changes to WAITING_FOR_INPUT.
            Current stage changes to the next one.
            After entering the additional required information the request state changes to PENDING & will be processed again (-> 1).

    4.
        All requests in READY_TO_APPROVE state may/should be processed be admins.
        By submitting they data to the third-party-consumer the request state should be changed to WAITING_FOR_APPROVAL.
    5.
        In case of successful approval the request state changes to DONE, otherwise to REJECTED.

 */
public enum ProcessingState {
    PENDING,                // stored, waiting for processing
    PROCESSING,             // processing
    FAILED,                 // processing failed, manual action required
    WAITING_FOR_INPUT,      // waiting for internal processes, manual action required
    SUSPENDED,              // processing suspended, waiting for automatically retry
    READY_TO_APPROVE,       // ready for external processes (offline), manual action required
    WAITING_FOR_APPROVAL,   // waiting for external processes (offline), dependency to third-party-suppliers
    REJECTED,               // processing rejected because of some external requirements, manual action required
    DONE                    // processing finished
}

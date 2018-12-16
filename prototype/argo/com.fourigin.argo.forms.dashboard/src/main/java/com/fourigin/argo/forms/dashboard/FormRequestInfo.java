package com.fourigin.argo.forms.dashboard;

import com.fourigin.argo.forms.AttachmentDescriptor;
import com.fourigin.argo.forms.models.FormsDataProcessingState;
import com.fourigin.argo.forms.models.ProcessingState;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class FormRequestInfo implements Serializable {
    private static final long serialVersionUID = -8317724026909376403L;

    private String id;
    private String formDefinition;
    private String customer;
    private String base;
    private long creationTimestamp;
    private ProcessingState state;
    private Set<AttachmentDescriptor> attachments;
    private FormsDataProcessingState processingState;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormDefinition() {
        return formDefinition;
    }

    public void setFormDefinition(String formDefinition) {
        this.formDefinition = formDefinition;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public ProcessingState getState() {
        return state;
    }

    public void setState(ProcessingState state) {
        this.state = state;
    }

    public Set<AttachmentDescriptor> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<AttachmentDescriptor> attachments) {
        this.attachments = attachments;
    }

    public FormsDataProcessingState getProcessingState() {
        return processingState;
    }

    public void setProcessingState(FormsDataProcessingState processingState) {
        this.processingState = processingState;
        if (processingState != null) {
            this.state = processingState.getState();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormRequestInfo)) return false;
        FormRequestInfo that = (FormRequestInfo) o;
        return creationTimestamp == that.creationTimestamp &&
            Objects.equals(id, that.id) &&
            Objects.equals(formDefinition, that.formDefinition) &&
            Objects.equals(customer, that.customer) &&
            Objects.equals(base, that.base) &&
            state == that.state &&
            Objects.equals(attachments, that.attachments) &&
            Objects.equals(processingState, that.processingState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, formDefinition, customer, base, creationTimestamp, state, attachments, processingState);
    }

    @Override
    public String toString() {
        return "FormRequestInfo{" +
            "id='" + id + '\'' +
            ", formDefinition='" + formDefinition + '\'' +
            ", customer='" + customer + '\'' +
            ", base='" + base + '\'' +
            ", creationTimestamp=" + creationTimestamp +
            ", state=" + state +
            ", attachments=" + attachments +
            ", processingState=" + processingState +
            '}';
    }
}

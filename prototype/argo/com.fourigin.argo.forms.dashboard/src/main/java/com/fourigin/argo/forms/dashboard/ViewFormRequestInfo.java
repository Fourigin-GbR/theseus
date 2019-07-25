package com.fourigin.argo.forms.dashboard;

import com.fourigin.argo.forms.AttachmentDescriptor;
import com.fourigin.argo.forms.models.FormsDataProcessingRecord;
import com.fourigin.argo.forms.models.ProcessingState;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class ViewFormRequestInfo implements Serializable {
    private static final long serialVersionUID = -8317724026909376403L;

    private String id;
    private String formDefinition;
    private String stage;
    private String customer;
    private String customerName;
    private String requestData;
    private String base;
    private long creationTimestamp;
    private ProcessingState state;
    private Set<AttachmentDescriptor> attachments;
    private FormsDataProcessingRecord processingRecord;

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

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
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

    public FormsDataProcessingRecord getProcessingState() {
        return processingRecord;
    }

    public void setProcessingState(FormsDataProcessingRecord processingState) {
        this.processingRecord = processingState;
        if (processingState != null) {
            this.state = processingState.getState();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewFormRequestInfo that = (ViewFormRequestInfo) o;
        return creationTimestamp == that.creationTimestamp &&
                Objects.equals(id, that.id) &&
                Objects.equals(formDefinition, that.formDefinition) &&
                Objects.equals(stage, that.stage) &&
                Objects.equals(customer, that.customer) &&
                Objects.equals(customerName, that.customerName) &&
                Objects.equals(requestData, that.requestData) &&
                Objects.equals(base, that.base) &&
                state == that.state &&
                Objects.equals(attachments, that.attachments) &&
                Objects.equals(processingRecord, that.processingRecord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, formDefinition, stage, customer, customerName, requestData, base, creationTimestamp, state, attachments, processingRecord);
    }

    @Override
    public String toString() {
        return "ViewFormRequestInfo{" +
                "id='" + id + '\'' +
                ", formDefinition='" + formDefinition + '\'' +
                ", stage='" + stage + '\'' +
                ", customer='" + customer + '\'' +
                ", customerName='" + customerName + '\'' +
                ", requestData='" + requestData + '\'' +
                ", base='" + base + '\'' +
                ", creationTimestamp=" + creationTimestamp +
                ", state=" + state +
                ", attachments=" + attachments +
                ", processingRecord=" + processingRecord +
                '}';
    }
}

package com.fourigin.argo.forms.dashboard;

import com.fourigin.argo.forms.AttachmentDescriptor;

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
    private Set<AttachmentDescriptor> attachments;

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

    public Set<AttachmentDescriptor> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<AttachmentDescriptor> attachments) {
        this.attachments = attachments;
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
            Objects.equals(attachments, that.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, formDefinition, customer, base, creationTimestamp, attachments);
    }

    @Override
    public String toString() {
        return "FormRequestInfo{" +
            "id='" + id + '\'' +
            ", formDefinition='" + formDefinition + '\'' +
            ", customer='" + customer + '\'' +
            ", base='" + base + '\'' +
            ", creationTimestamp=" + creationTimestamp +
            ", attachments=" + attachments +
            '}';
    }
}

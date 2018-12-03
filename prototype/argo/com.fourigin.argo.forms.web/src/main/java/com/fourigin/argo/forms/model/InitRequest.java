package com.fourigin.argo.forms.model;

import java.io.Serializable;
import java.util.Objects;

public class InitRequest implements Serializable {
    private static final long serialVersionUID = 3076639750885710397L;

    private String formDefinition;
    private String customer;
    private String entryId;

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

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InitRequest)) return false;
        InitRequest that = (InitRequest) o;
        return Objects.equals(formDefinition, that.formDefinition) &&
            Objects.equals(customer, that.customer) &&
            Objects.equals(entryId, that.entryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formDefinition, customer, entryId);
    }

    @Override
    public String toString() {
        return "InitRequest{" +
            "formDefinition='" + formDefinition + '\'' +
            ", customer='" + customer + '\'' +
            ", entryId='" + entryId + '\'' +
            '}';
    }
}

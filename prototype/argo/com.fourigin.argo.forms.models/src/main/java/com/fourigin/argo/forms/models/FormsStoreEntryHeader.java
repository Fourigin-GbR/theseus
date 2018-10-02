package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class FormsStoreEntryHeader implements Serializable {
    private static final long serialVersionUID = 7175957709964797404L;

    private String formDefinition;
    private String customer;
    private String base;
    private Map<String, String> referrer;

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

    public Map<String, String> getReferrer() {
        return referrer;
    }

    public void setReferrer(Map<String, String> referrer) {
        this.referrer = referrer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormsStoreEntryHeader)) return false;
        FormsStoreEntryHeader that = (FormsStoreEntryHeader) o;
        return Objects.equals(formDefinition, that.formDefinition) &&
            Objects.equals(customer, that.customer) &&
            Objects.equals(base, that.base) &&
            Objects.equals(referrer, that.referrer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formDefinition, customer, base, referrer);
    }

    @Override
    public String toString() {
        return "FormsRequestHeader{" +
            "formDefinition='" + formDefinition + '\'' +
            ", customer='" + customer + '\'' +
            ", base='" + base + '\'' +
            ", referrer=" + referrer +
            '}';
    }
}

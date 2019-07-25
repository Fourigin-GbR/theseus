package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FormsEntryHeader implements Serializable {
    private static final long serialVersionUID = 7175957709964797404L;

    private String formDefinition;
    private String stage;
    private String entryId;
    private String customerId;
    private String base;
    private Locale locale;
    private Map<String, String> referrer;

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

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
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
        if (o == null || getClass() != o.getClass()) return false;
        FormsEntryHeader that = (FormsEntryHeader) o;
        return Objects.equals(formDefinition, that.formDefinition) &&
                Objects.equals(stage, that.stage) &&
                Objects.equals(entryId, that.entryId) &&
                Objects.equals(customerId, that.customerId) &&
                Objects.equals(base, that.base) &&
                Objects.equals(locale, that.locale) &&
                Objects.equals(referrer, that.referrer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formDefinition, stage, entryId, customerId, base, locale, referrer);
    }

    @Override
    public String toString() {
        return "FormsEntryHeader{" +
                "formDefinition='" + formDefinition + '\'' +
                ", stage='" + stage + '\'' +
                ", entryId='" + entryId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", base='" + base + '\'' +
                ", locale=" + locale +
                ", referrer=" + referrer +
                '}';
    }
}

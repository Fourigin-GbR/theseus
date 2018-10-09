package com.fourigin.argo.forms.model;

import com.fourigin.argo.forms.models.FormsEntryHeader;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class FormsRequest implements Serializable {
    private static final long serialVersionUID = 940469824505958177L;

    private String formId;

    private FormsEntryHeader header;

    private Map<String, String> data;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public FormsEntryHeader getHeader() {
        return header;
    }

    public void setHeader(FormsEntryHeader header) {
        this.header = header;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormsRequest)) return false;
        FormsRequest that = (FormsRequest) o;
        return Objects.equals(formId, that.formId) &&
            Objects.equals(header, that.header) &&
            Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formId, header, data);
    }

    @Override
    public String toString() {
        return "FormsRequest{" +
            "formId='" + formId + '\'' +
            ", header=" + header +
            ", data=" + data +
            '}';
    }
}

package com.fourigin.argo.forms.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class CleanupRequest implements Serializable {
    private static final long serialVersionUID = -6100159298651158212L;

    private String formDefinitionId;
    private Map<String, String> data;

    public String getFormDefinitionId() {
        return formDefinitionId;
    }

    public void setFormDefinitionId(String formDefinitionId) {
        this.formDefinitionId = formDefinitionId;
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
        if (!(o instanceof CleanupRequest)) return false;
        CleanupRequest that = (CleanupRequest) o;
        return Objects.equals(formDefinitionId, that.formDefinitionId) &&
            Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formDefinitionId, data);
    }

    @Override
    public String toString() {
        return "CleanupRequest{" +
            "formDefinitionId='" + formDefinitionId + '\'' +
            ", data=" + data +
            '}';
    }
}

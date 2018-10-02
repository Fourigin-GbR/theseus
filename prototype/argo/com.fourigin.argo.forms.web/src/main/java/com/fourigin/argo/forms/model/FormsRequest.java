package com.fourigin.argo.forms.model;

import com.fourigin.argo.forms.models.FormsStoreEntryHeader;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class FormsRequest implements Serializable {
    private static final long serialVersionUID = 940469824505958177L;

    private FormsStoreEntryHeader header;

    private Map<String, Object> data;

    public FormsStoreEntryHeader getHeader() {
        return header;
    }

    public void setHeader(FormsStoreEntryHeader header) {
        this.header = header;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormsRequest)) return false;
        FormsRequest that = (FormsRequest) o;
        return Objects.equals(header, that.header) &&
            Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, data);
    }

    @Override
    public String toString() {
        return "FormsRequest{" +
            "header=" + header +
            ", data=" + data +
            '}';
    }
}

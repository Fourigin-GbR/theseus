package com.fourigin.argo.forms.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FormsStoreEntryInfo implements Serializable {
    private static final long serialVersionUID = 899512210680454843L;

    private String id;

    private String revision;

    private FormsRequestHeader header;

    private List<FormsDataProcessorState> states;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public FormsRequestHeader getHeader() {
        return header;
    }

    public void setHeader(FormsRequestHeader header) {
        this.header = header;
    }

    public List<FormsDataProcessorState> getStates() {
        return states;
    }

    public void setStates(List<FormsDataProcessorState> states) {
        this.states = states;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormsStoreEntryInfo)) return false;
        FormsStoreEntryInfo that = (FormsStoreEntryInfo) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(revision, that.revision) &&
            Objects.equals(header, that.header) &&
            Objects.equals(states, that.states);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, revision, header, states);
    }

    @Override
    public String toString() {
        return "FormsStoreEntryInfo{" +
            "id='" + id + '\'' +
            ", revision='" + revision + '\'' +
            ", header=" + header +
            ", states=" + states +
            '}';
    }
}

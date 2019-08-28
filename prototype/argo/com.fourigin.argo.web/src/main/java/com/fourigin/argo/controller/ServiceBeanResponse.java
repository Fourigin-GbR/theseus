package com.fourigin.argo.controller;

import java.io.Serializable;
import java.util.Objects;

public class ServiceBeanResponse<T> extends ServiceResponse implements Serializable {
    private static final long serialVersionUID = -3302269923322102550L;

    private String revision;
    private T payload;

    public ServiceBeanResponse(T payload, String revision, int status) {
        super(status);
        this.payload = payload;
        this.revision = revision;
    }

    public T getPayload() {
        return payload;
    }

    public String getRevision() {
        return revision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ServiceBeanResponse<?> that = (ServiceBeanResponse<?>) o;
        return Objects.equals(revision, that.revision) &&
                Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), revision, payload);
    }

    @Override
    public String toString() {
        return "ServiceBeanResponse{" +
                "revision='" + revision + '\'' +
                ", payload=" + payload +
                '}';
    }
}

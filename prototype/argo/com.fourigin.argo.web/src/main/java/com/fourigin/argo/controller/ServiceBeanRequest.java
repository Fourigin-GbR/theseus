package com.fourigin.argo.controller;

import java.io.Serializable;
import java.util.Objects;

public class ServiceBeanRequest<T> implements Serializable {
    private static final long serialVersionUID = -3302269923322102550L;

    private String revision;
    private T payload;

    public ServiceBeanRequest(T payload, String revision) {
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
        ServiceBeanRequest<?> that = (ServiceBeanRequest<?>) o;
        return Objects.equals(revision, that.revision) &&
                Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(revision, payload);
    }

    @Override
    public String toString() {
        return "ServiceBeanRequest{" +
                "revision='" + revision + '\'' +
                ", payload=" + payload +
                '}';
    }
}

package com.fourigin.argo.forms.models;

import java.io.Serializable;
import java.util.Objects;

public class Attachment implements Serializable {
    private static final long serialVersionUID = 3916763327263009607L;

    private String producer;
    private long timestamp;
    private Object payload;

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attachment)) return false;
        Attachment that = (Attachment) o;
        return timestamp == that.timestamp &&
            Objects.equals(producer, that.producer) &&
            Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(producer, timestamp, payload);
    }

    @Override
    public String toString() {
        return "Attachment{" +
            "producer='" + producer + '\'' +
            ", timestamp=" + timestamp +
            ", payload=" + payload +
            '}';
    }
}

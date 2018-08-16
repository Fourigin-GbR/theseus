package com.fourigin.argo.models.structure;

import java.util.Objects;

public class CompileState {
    private String checksum;
    private boolean compiled;
    private long timestamp;
    private String message;

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public boolean isCompiled() {
        return compiled;
    }

    public void setCompiled(boolean compiled) {
        this.compiled = compiled;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompileState)) return false;
        CompileState that = (CompileState) o;
        return compiled == that.compiled &&
            timestamp == that.timestamp &&
            Objects.equals(checksum, that.checksum) &&
            Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checksum, compiled, timestamp, message);
    }

    @Override
    public String toString() {
        return "CompileState{" +
          "checksum='" + checksum + '\'' +
          ", compiled=" + compiled +
          ", timestamp=" + timestamp +
          ", message='" + message + '\'' +
          '}';
    }
}

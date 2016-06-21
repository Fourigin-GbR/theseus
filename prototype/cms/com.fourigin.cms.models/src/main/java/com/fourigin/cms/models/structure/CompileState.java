package com.fourigin.cms.models.structure;

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
    public String toString() {
        return "CompileState{" +
          "checksum='" + checksum + '\'' +
          ", compiled=" + compiled +
          ", timestamp=" + timestamp +
          ", message='" + message + '\'' +
          '}';
    }
}

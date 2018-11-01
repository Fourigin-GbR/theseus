package com.fourigin.argo.forms;

import java.util.Objects;

public class AttachmentDescriptor {
    private String name;
    private String filename;
    private String mimeType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttachmentDescriptor)) return false;
        AttachmentDescriptor that = (AttachmentDescriptor) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(filename, that.filename) &&
            Objects.equals(mimeType, that.mimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, filename, mimeType);
    }

    @Override
    public String toString() {
        return "AttachmentDescriptor{" +
            "name='" + name + '\'' +
            ", filename='" + filename + '\'' +
            ", mimeType='" + mimeType + '\'' +
            '}';
    }
}

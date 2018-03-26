package com.fourigin.argo.editors;

import com.fourigin.argo.models.content.elements.ContentElement;

import java.io.Serializable;
import java.util.Objects;

public class StatusAwareContentElementResponse extends AbstractContentElementPointer implements Serializable, ContentElementPointer {

    private static final long serialVersionUID = -6840096148919100384L;
    
    private String base;
    private String path;
    private String contentPath;
    private boolean status;
    private String currentChecksum;
    private ContentElement currentContentElement;

    @Override
    public String getBase() {
        return base;
    }

    @Override
    public void setBase(String base) {
        this.base = base;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getContentPath() {
        return contentPath;
    }

    @Override
    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getCurrentChecksum() {
        return currentChecksum;
    }

    public void setCurrentChecksum(String currentChecksum) {
        this.currentChecksum = currentChecksum;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ContentElement getCurrentContentElement() {
        return currentContentElement;
    }

    public void setCurrentContentElement(ContentElement currentContentElement) {
        this.currentContentElement = currentContentElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatusAwareContentElementResponse)) return false;
        StatusAwareContentElementResponse that = (StatusAwareContentElementResponse) o;
        return status == that.status &&
            Objects.equals(base, that.base) &&
            Objects.equals(path, that.path) &&
            Objects.equals(contentPath, that.contentPath) &&
            Objects.equals(currentChecksum, that.currentChecksum) &&
            Objects.equals(currentContentElement, that.currentContentElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, path, contentPath, status, currentChecksum, currentContentElement);
    }

    @Override
    public String toString() {
        return "StatusAwareContentElementResponse{" +
            "base='" + base + '\'' +
            ", path='" + path + '\'' +
            ", contentPath='" + contentPath + '\'' +
            ", status=" + status +
            ", currentChecksum='" + currentChecksum + '\'' +
            ", currentContentElement=" + currentContentElement +
            '}';
    }
}

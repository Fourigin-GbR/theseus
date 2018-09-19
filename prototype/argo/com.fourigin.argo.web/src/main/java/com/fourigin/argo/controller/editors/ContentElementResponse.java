package com.fourigin.argo.controller.editors;

import com.fourigin.argo.models.content.elements.ContentElement;

import java.io.Serializable;
import java.util.Objects;

public class ContentElementResponse extends AbstractContentElementPointer implements Serializable, ContentElementPointer {

    private static final long serialVersionUID = -4741774244660755370L;

    private String base;
    private String path;
    private String contentPath;
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

    public ContentElement getCurrentContentElement() {
        return currentContentElement;
    }

    public void setCurrentContentElement(ContentElement currentContentElement) {
        this.currentContentElement = currentContentElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentElementResponse)) return false;
        ContentElementResponse that = (ContentElementResponse) o;
        return Objects.equals(base, that.base) &&
            Objects.equals(path, that.path) &&
            Objects.equals(contentPath, that.contentPath) &&
            Objects.equals(currentChecksum, that.currentChecksum) &&
            Objects.equals(currentContentElement, that.currentContentElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, path, contentPath, currentChecksum, currentContentElement);
    }

    @Override
    public String toString() {
        return "ContentElementResponse{" +
            "base='" + base + '\'' +
            ", path='" + path + '\'' +
            ", contentPath='" + contentPath + '\'' +
            ", currentChecksum='" + currentChecksum + '\'' +
            ", currentContentElement=" + currentContentElement +
            '}';
    }
}

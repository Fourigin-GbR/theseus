package com.fourigin.argo.controller.editors;

import com.fourigin.argo.models.content.elements.ContentElement;

import java.io.Serializable;
import java.util.Objects;

public class SaveChangesRequest extends AbstractContentElementPointer implements Serializable, ContentElementPointer {
    private static final long serialVersionUID = -5738688677326787541L;

    private String base;
    private String path;
    private String contentPath;
    private String originalChecksum;
    private ContentElement modifiedContentElement;

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

    public String getOriginalChecksum() {
        return originalChecksum;
    }

    public void setOriginalChecksum(String originalChecksum) {
        this.originalChecksum = originalChecksum;
    }

    public ContentElement getModifiedContentElement() {
        return modifiedContentElement;
    }

    public void setModifiedContentElement(ContentElement modifiedContentElement) {
        this.modifiedContentElement = modifiedContentElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SaveChangesRequest)) return false;
        SaveChangesRequest that = (SaveChangesRequest) o;
        return Objects.equals(base, that.base) &&
            Objects.equals(path, that.path) &&
            Objects.equals(contentPath, that.contentPath) &&
            Objects.equals(originalChecksum, that.originalChecksum) &&
            Objects.equals(modifiedContentElement, that.modifiedContentElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, path, contentPath, originalChecksum, modifiedContentElement);
    }

    @Override
    public String toString() {
        return "SaveChangesRequest{" +
            "base='" + base + '\'' +
            ", path='" + path + '\'' +
            ", contentPath='" + contentPath + '\'' +
            ", originalChecksum='" + originalChecksum + '\'' +
            ", modifiedContentElement=" + modifiedContentElement +
            '}';
    }
}

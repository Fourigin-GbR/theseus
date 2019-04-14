package com.fourigin.argo.controller.editors;

import com.fourigin.argo.models.content.elements.ContentElement;

import java.io.Serializable;
import java.util.Objects;

public class SaveChangesRequest extends AbstractContentElementPointer implements Serializable, ContentElementPointer {
    private static final long serialVersionUID = -5738688677326787541L;

    private String language;
    private String path;
    private String contentPath;
    private String originalChecksum;
    private ContentElement modifiedContentElement;

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(String locale) {
        this.language = locale;
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
        return Objects.equals(language, that.language) &&
            Objects.equals(path, that.path) &&
            Objects.equals(contentPath, that.contentPath) &&
            Objects.equals(originalChecksum, that.originalChecksum) &&
            Objects.equals(modifiedContentElement, that.modifiedContentElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, path, contentPath, originalChecksum, modifiedContentElement);
    }

    @Override
    public String toString() {
        return "SaveChangesRequest{" +
            "language='" + language + '\'' +
            ", path='" + path + '\'' +
            ", contentPath='" + contentPath + '\'' +
            ", originalChecksum='" + originalChecksum + '\'' +
            ", modifiedContentElement=" + modifiedContentElement +
            '}';
    }
}

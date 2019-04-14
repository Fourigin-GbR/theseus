package com.fourigin.argo.controller.editors;

import java.io.Serializable;
import java.util.Objects;

public class RetrieveContentRequest extends AbstractContentElementPointer implements Serializable, ContentElementPointer {
    private static final long serialVersionUID = -1532883135997976347L;

    private String language;
    private String path;
    private String contentPath;

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RetrieveContentRequest)) return false;
        RetrieveContentRequest that = (RetrieveContentRequest) o;
        return Objects.equals(language, that.language) &&
            Objects.equals(path, that.path) &&
            Objects.equals(contentPath, that.contentPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, path, contentPath);
    }

    @Override
    public String toString() {
        return "RetrieveContentRequest{" +
            "language='" + language + '\'' +
            ", path='" + path + '\'' +
            ", contentPath='" + contentPath + '\'' +
            '}';
    }
}

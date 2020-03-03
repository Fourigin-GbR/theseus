package com.fourigin.argo.models.action;

import java.util.Objects;

public class ContentReference {
    private String language;
    private String path;
    private String contentPath;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentReference that = (ContentReference) o;
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
        return "ContentReference{" +
                "language='" + language + '\'' +
                ", path='" + path + '\'' +
                ", contentPath='" + contentPath + '\'' +
                '}';
    }
}

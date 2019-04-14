package com.fourigin.argo.controller.editors;

import java.io.Serializable;
import java.util.Objects;

public class UpToDateRequest extends AbstractContentElementPointer implements Serializable, ContentElementPointer {
    private static final long serialVersionUID = -1532883135997976347L;

    private String language;
    private String path;
    private String contentPath;
    private String checksum;

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

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpToDateRequest)) return false;
        UpToDateRequest that = (UpToDateRequest) o;
        return Objects.equals(language, that.language) &&
            Objects.equals(path, that.path) &&
            Objects.equals(contentPath, that.contentPath) &&
            Objects.equals(checksum, that.checksum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, path, contentPath, checksum);
    }

    @Override
    public String toString() {
        return "UpToDateRequest{" +
            "language='" + language + '\'' +
            ", path='" + path + '\'' +
            ", contentPath='" + contentPath + '\'' +
            ", checksum='" + checksum + '\'' +
            '}';
    }
}

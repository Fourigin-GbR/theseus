package com.fourigin.argo.controller.editors.models;

import java.io.Serializable;
import java.util.Objects;

public class SiteNode implements Serializable {
    private static final long serialVersionUID = -1073811604906400190L;

    private String language;
    private String path;
    private SiteNodeContent content;

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

    public SiteNodeContent getContent() {
        return content;
    }

    public void setContent(SiteNodeContent content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteNode siteNode = (SiteNode) o;
        return Objects.equals(language, siteNode.language) &&
                Objects.equals(path, siteNode.path) &&
                Objects.equals(content, siteNode.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, path, content);
    }

    @Override
    public String toString() {
        return "SiteNode{" +
                "language='" + language + '\'' +
                ", path='" + path + '\'' +
                ", content=" + content +
                '}';
    }
}

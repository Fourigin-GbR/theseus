package com.fourigin.argo.controller.editors;

import java.io.Serializable;
import java.util.Objects;

public class SiteNode implements Serializable {
    private static final long serialVersionUID = -1073811604906400190L;

    private String path;
    private String originalChecksum;
    private SiteNodeContent content;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOriginalChecksum() {
        return originalChecksum;
    }

    public void setOriginalChecksum(String originalChecksum) {
        this.originalChecksum = originalChecksum;
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
        return Objects.equals(path, siteNode.path) &&
                Objects.equals(originalChecksum, siteNode.originalChecksum) &&
                Objects.equals(content, siteNode.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, originalChecksum, content);
    }

    @Override
    public String toString() {
        return "SiteNode{" +
                "path='" + path + '\'' +
                ", currentChecksum='" + originalChecksum + '\'' +
                ", content=" + content +
                '}';
    }
}

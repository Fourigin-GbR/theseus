package com.fourigin.argo.editors;

import com.fourigin.argo.models.content.elements.ContentElement;

import java.io.Serializable;

public class ContentElementResponse extends AbstractContentElementPointer implements Serializable, ContentElementPointer {

    private static final long serialVersionUID = -4741774244660755370L;

    private String base;
    private String siteStructurePath;
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

    @Override
    public String getSiteStructurePath() {
        return siteStructurePath;
    }

    @Override
    public void setSiteStructurePath(String siteStructurePath) {
        this.siteStructurePath = siteStructurePath;
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

        if (base != null ? !base.equals(that.base) : that.base != null) return false;
        if (siteStructurePath != null ? !siteStructurePath.equals(that.siteStructurePath) : that.siteStructurePath != null)
            return false;
        if (contentPath != null ? !contentPath.equals(that.contentPath) : that.contentPath != null) return false;
        //noinspection SimplifiableIfStatement
        if (currentChecksum != null ? !currentChecksum.equals(that.currentChecksum) : that.currentChecksum != null)
            return false;
        return currentContentElement != null ? currentContentElement.equals(that.currentContentElement) : that.currentContentElement == null;
    }

    @Override
    public int hashCode() {
        int result = base != null ? base.hashCode() : 0;
        result = 31 * result + (siteStructurePath != null ? siteStructurePath.hashCode() : 0);
        result = 31 * result + (contentPath != null ? contentPath.hashCode() : 0);
        result = 31 * result + (currentChecksum != null ? currentChecksum.hashCode() : 0);
        result = 31 * result + (currentContentElement != null ? currentContentElement.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContentElementResponse{" +
            "base='" + base + '\'' +
            ", siteStructurePath='" + siteStructurePath + '\'' +
            ", contentPath='" + contentPath + '\'' +
            ", currentChecksum='" + currentChecksum + '\'' +
            ", currentContentElement=" + currentContentElement +
            '}';
    }

}

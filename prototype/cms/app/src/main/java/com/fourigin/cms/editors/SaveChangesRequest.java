package com.fourigin.cms.editors;

import com.fourigin.cms.models.content.elements.ContentElement;

import java.io.Serializable;

public class SaveChangesRequest extends AbstractContentElementPointer implements Serializable, ContentElementPointer {
    private static final long serialVersionUID = -5738688677326787541L;

    private String siteStructurePath;
    private String contentPath;
    private String originalChecksum;
    private ContentElement modifiedContentElement;

    public String getSiteStructurePath() {
        return siteStructurePath;
    }

    public void setSiteStructurePath(String siteStructurePath) {
        this.siteStructurePath = siteStructurePath;
    }

    public String getContentPath() {
        return contentPath;
    }

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

        if (siteStructurePath != null ? !siteStructurePath.equals(that.siteStructurePath) : that.siteStructurePath != null)
            return false;
        if (contentPath != null ? !contentPath.equals(that.contentPath) : that.contentPath != null) return false;
        //noinspection SimplifiableIfStatement
        if (originalChecksum != null ? !originalChecksum.equals(that.originalChecksum) : that.originalChecksum != null)
            return false;
        return modifiedContentElement != null ? modifiedContentElement.equals(that.modifiedContentElement) : that.modifiedContentElement == null;

    }

    @Override
    public int hashCode() {
        int result = siteStructurePath != null ? siteStructurePath.hashCode() : 0;
        result = 31 * result + (contentPath != null ? contentPath.hashCode() : 0);
        result = 31 * result + (originalChecksum != null ? originalChecksum.hashCode() : 0);
        result = 31 * result + (modifiedContentElement != null ? modifiedContentElement.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SaveChangesRequest{" +
          "siteStructurePath='" + siteStructurePath + '\'' +
          ", contentPath='" + contentPath + '\'' +
          ", originalChecksum='" + originalChecksum + '\'' +
          ", modifiedContentElement=" + modifiedContentElement +
          '}';
    }
}

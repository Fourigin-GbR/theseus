package com.fourigin.cms.editors;

import com.fourigin.cms.models.content.elements.ContentElement;

import java.io.Serializable;

public class UpToDateResponse extends AbstractContentElementPointer implements Serializable, ContentElementPointer {
    private static final long serialVersionUID = 3131763107177661718L;

    private String siteStructurePath;
    private String contentPath;
    private boolean upToDate;
    private String currentChecksum;
    private ContentElement currentContentElement;

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

    public String getCurrentChecksum() {
        return currentChecksum;
    }

    public void setCurrentChecksum(String currentChecksum) {
        this.currentChecksum = currentChecksum;
    }

    public boolean isUpToDate() {
        return upToDate;
    }

    public void setUpToDate(boolean upToDate) {
        this.upToDate = upToDate;
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
        if (!(o instanceof UpToDateResponse)) return false;

        UpToDateResponse that = (UpToDateResponse) o;

        if (upToDate != that.upToDate) return false;
        if (siteStructurePath != null ? !siteStructurePath.equals(that.siteStructurePath) : that.siteStructurePath != null)
            return false;
        if (contentPath != null ? !contentPath.equals(that.contentPath) : that.contentPath != null) return false;
        //noinspection SimplifiableIfStatement
        if (currentChecksum != null ? !currentChecksum.equals(that.currentChecksum) : that.currentChecksum != null) return false;
        return currentContentElement != null ? currentContentElement.equals(that.currentContentElement) : that.currentContentElement == null;

    }

    @Override
    public int hashCode() {
        int result = siteStructurePath != null ? siteStructurePath.hashCode() : 0;
        result = 31 * result + (contentPath != null ? contentPath.hashCode() : 0);
        result = 31 * result + (currentChecksum != null ? currentChecksum.hashCode() : 0);
        result = 31 * result + (upToDate ? 1 : 0);
        result = 31 * result + (currentContentElement != null ? currentContentElement.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UpToDateResponse{" +
          "siteStructurePath='" + siteStructurePath + '\'' +
          ", contentPath='" + contentPath + '\'' +
          ", currentChecksum='" + currentChecksum + '\'' +
          ", upToDate=" + upToDate +
          ", currentContentElement=" + currentContentElement +
          '}';
    }
}

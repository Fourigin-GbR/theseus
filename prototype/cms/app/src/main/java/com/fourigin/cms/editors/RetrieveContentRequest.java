package com.fourigin.cms.editors;

import java.io.Serializable;

public class RetrieveContentRequest extends AbstractContentElementPointer implements Serializable, ContentElementPointer {
    private static final long serialVersionUID = -1532883135997976347L;

    private String siteStructurePath;
    private String contentPath;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RetrieveContentRequest)) return false;

        RetrieveContentRequest that = (RetrieveContentRequest) o;

        //noinspection SimplifiableIfStatement
        if (siteStructurePath != null ? !siteStructurePath.equals(that.siteStructurePath) : that.siteStructurePath != null)
            return false;
        return contentPath != null ? contentPath.equals(that.contentPath) : that.contentPath == null;
    }

    @Override
    public int hashCode() {
        int result = siteStructurePath != null ? siteStructurePath.hashCode() : 0;
        result = 31 * result + (contentPath != null ? contentPath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RetrieveContentRequest{" +
          "siteStructurePath='" + siteStructurePath + '\'' +
          ", contentPath='" + contentPath + '\'' +
          '}';
    }

}

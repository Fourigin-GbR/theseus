package com.fourigin.cms.editors;

import java.io.Serializable;

public class UpToDateRequest extends AbstractContentElementPointer implements Serializable, ContentElementPointer {
    private static final long serialVersionUID = -1532883135997976347L;

    private String base;
    private String siteStructurePath;
    private String contentPath;
    private String checksum;

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

        if (base != null ? !base.equals(that.base) : that.base != null) return false;
        if (siteStructurePath != null ? !siteStructurePath.equals(that.siteStructurePath) : that.siteStructurePath != null)
            return false;
        //noinspection SimplifiableIfStatement
        if (contentPath != null ? !contentPath.equals(that.contentPath) : that.contentPath != null) return false;
        return checksum != null ? checksum.equals(that.checksum) : that.checksum == null;
    }

    @Override
    public int hashCode() {
        int result = base != null ? base.hashCode() : 0;
        result = 31 * result + (siteStructurePath != null ? siteStructurePath.hashCode() : 0);
        result = 31 * result + (contentPath != null ? contentPath.hashCode() : 0);
        result = 31 * result + (checksum != null ? checksum.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UpToDateRequest{" +
            "base='" + base + '\'' +
            ", siteStructurePath='" + siteStructurePath + '\'' +
            ", contentPath='" + contentPath + '\'' +
            ", checksum='" + checksum + '\'' +
            '}';
    }

}

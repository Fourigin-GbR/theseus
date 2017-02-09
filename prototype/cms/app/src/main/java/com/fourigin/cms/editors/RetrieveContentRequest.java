package com.fourigin.cms.editors;

import java.io.Serializable;

public class RetrieveContentRequest extends AbstractContentElementPointer implements Serializable, ContentElementPointer {
    private static final long serialVersionUID = -1532883135997976347L;

    private String base;
    private String siteStructurePath;
    private String contentPath;
    private boolean flushCaches = false;

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

    public boolean isFlushCaches() {
        return flushCaches;
    }

    public void setFlushCaches(boolean flushCaches) {
        this.flushCaches = flushCaches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RetrieveContentRequest)) return false;

        RetrieveContentRequest request = (RetrieveContentRequest) o;

        if (flushCaches != request.flushCaches) return false;
        if (base != null ? !base.equals(request.base) : request.base != null) return false;
        //noinspection SimplifiableIfStatement
        if (siteStructurePath != null ? !siteStructurePath.equals(request.siteStructurePath) : request.siteStructurePath != null)
            return false;
        return contentPath != null ? contentPath.equals(request.contentPath) : request.contentPath == null;
    }

    @Override
    public int hashCode() {
        int result = base != null ? base.hashCode() : 0;
        result = 31 * result + (siteStructurePath != null ? siteStructurePath.hashCode() : 0);
        result = 31 * result + (contentPath != null ? contentPath.hashCode() : 0);
        result = 31 * result + (flushCaches ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RetrieveContentRequest{" +
            "base='" + base + '\'' +
            ", siteStructurePath='" + siteStructurePath + '\'' +
            ", contentPath='" + contentPath + '\'' +
            ", flushCaches=" + flushCaches +
            '}';
    }

}

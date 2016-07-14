package com.fourigin.cms.editors;

public interface ContentElementPointer {
    String getSiteStructurePath();

    void setSiteStructurePath(String siteStructurePath);

    String getContentPath();

    void setContentPath(String contentPath);

    void copyFrom(ContentElementPointer otherPointer);
}

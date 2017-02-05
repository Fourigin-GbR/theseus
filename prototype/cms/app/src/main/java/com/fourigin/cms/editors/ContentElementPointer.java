package com.fourigin.cms.editors;

public interface ContentElementPointer {
    String getBase();

    void setBase(String base);

    String getSiteStructurePath();

    void setSiteStructurePath(String siteStructurePath);

    String getContentPath();

    void setContentPath(String contentPath);

    void copyFrom(ContentElementPointer otherPointer);
}

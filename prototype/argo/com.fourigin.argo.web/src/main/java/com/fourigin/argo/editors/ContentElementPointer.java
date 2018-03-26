package com.fourigin.argo.editors;

public interface ContentElementPointer {
    String getBase();

    void setBase(String base);

    String getPath();

    void setPath(String path);

    String getContentPath();

    void setContentPath(String contentPath);

    void copyFrom(ContentElementPointer otherPointer);
}

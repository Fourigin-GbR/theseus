package com.fourigin.argo.controller.editors;

public interface ContentElementPointer {
    String getLanguage();

    void setLanguage(String language);

    String getPath();

    void setPath(String path);

    String getContentPath();

    void setContentPath(String contentPath);

    void copyFrom(ContentElementPointer otherPointer);
}

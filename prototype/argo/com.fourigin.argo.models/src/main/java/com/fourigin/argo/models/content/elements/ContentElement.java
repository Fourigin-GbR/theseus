package com.fourigin.argo.models.content.elements;

import java.io.Serializable;
import java.util.Map;

public interface ContentElement extends Serializable {
    String getName();
    void setName(String name);

    String getTitle();
    void setTitle(String title);

    Map<String, String> getAttributes();
    void setAttributes(Map<String, String> attributes);
    void setAttribute(String key, String value);
}

package com.fourigin.argo.models.content.elements.list;

import java.io.Serializable;
import java.util.Map;

public interface ContentListElement extends Serializable {
    String getTitle();
    void setTitle(String title);

    Map<String, String> getAttributes();
    void setAttributes(Map<String, String> attributes);
    void setAttribute(String key, String value);
}

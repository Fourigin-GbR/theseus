package com.fourigin.argo.models.content.elements;

import java.io.Serializable;
import java.util.Map;

public interface AttributesAwareContentElement extends Serializable {
    Map<String, String> getAttributes();
    void setAttributes(Map<String, String> attributes);
    void setAttribute(String key, String value);

    Map<String, Object> getTypeProperties();
    void setTypeProperties(Map<String, Object> typeProperties);
    void setTypeProperty(String key, Object value);
}

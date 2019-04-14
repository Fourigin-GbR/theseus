package com.fourigin.argo.models.content.elements;

import java.io.Serializable;

public interface ContentElement extends Serializable, AttributesAwareContentElement {
    String getName();
    void setName(String name);
}

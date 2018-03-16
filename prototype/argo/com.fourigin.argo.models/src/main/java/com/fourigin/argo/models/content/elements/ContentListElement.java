package com.fourigin.argo.models.content.elements;

import java.io.Serializable;

public interface ContentListElement extends Serializable, AttributesAwareContentElement {
    String getTitle();
    void setTitle(String title);
}

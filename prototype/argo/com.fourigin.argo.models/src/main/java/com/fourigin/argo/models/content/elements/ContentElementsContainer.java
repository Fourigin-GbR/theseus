package com.fourigin.argo.models.content.elements;

import java.util.List;

public interface ContentElementsContainer<T> extends AttributesAwareContentElement {
    List<T> getElements();

    void setElements(List<T> elements);
}

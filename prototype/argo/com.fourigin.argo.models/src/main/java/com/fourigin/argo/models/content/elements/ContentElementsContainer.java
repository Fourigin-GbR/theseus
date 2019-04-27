package com.fourigin.argo.models.content.elements;

import java.util.List;

public interface ContentElementsContainer extends AttributesAwareContentElement {
    List<ContentElement> getElements();

    void setElements(List<ContentElement> elements);
}

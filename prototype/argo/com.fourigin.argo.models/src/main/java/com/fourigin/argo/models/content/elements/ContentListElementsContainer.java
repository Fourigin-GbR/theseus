package com.fourigin.argo.models.content.elements;

import java.util.List;

public interface ContentListElementsContainer extends AttributesAwareContentElement {
    List<ListElement> getElements();

    void setElements(List<ListElement> elements);
}

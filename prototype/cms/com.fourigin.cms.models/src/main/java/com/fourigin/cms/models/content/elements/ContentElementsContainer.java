package com.fourigin.cms.models.content.elements;

import java.util.List;

public interface ContentElementsContainer {
    List<ContentElement> getElements();

    void setElements(List<ContentElement> elements);
}

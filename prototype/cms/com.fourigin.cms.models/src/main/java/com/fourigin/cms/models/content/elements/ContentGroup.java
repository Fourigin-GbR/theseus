package com.fourigin.cms.models.content.elements;

import java.util.List;

public class ContentGroup extends AbstractContentElement implements ContentElement {
    private List<ContentElement> elements;

    public List<ContentElement> getElements() {
        return elements;
    }

    public void setElements(List<ContentElement> elements) {
        this.elements = elements;
    }
}

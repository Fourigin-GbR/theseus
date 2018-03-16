package com.fourigin.argo.models.content.hotspots;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class DefaultElementsContainerEditorProperties extends DefaultElementEditorProperties implements Serializable, ElementsContainerEditorProperties {
    private static final long serialVersionUID = -2344610182324935068L;

    private Map<String, ElementEditorProperties> elements;

    @Override
    public Map<String, ElementEditorProperties> getElements() {
        return null;
    }

    public void setElements(Map<String, ElementEditorProperties> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultElementsContainerEditorProperties)) return false;
        if (!super.equals(o)) return false;
        DefaultElementsContainerEditorProperties that = (DefaultElementsContainerEditorProperties) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elements);
    }

    @Override
    public String toString() {
        return "DefaultElementsContainerEditorProperties{" +
            "elements=" + elements +
            '}';
    }
}

package com.fourigin.argo.models.content.hotspots;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class ElementsEditorProperties implements Serializable {
    private static final long serialVersionUID = -2344610182324935068L;

    private String title;

    private String description;

    private Map<String, ElementsEditorProperties> elements;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, ElementsEditorProperties> getElements() {
        return elements;
    }

    public void setElements(Map<String, ElementsEditorProperties> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementsEditorProperties)) return false;
        ElementsEditorProperties that = (ElementsEditorProperties) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, elements);
    }

    @Override
    public String toString() {
        return "ElementsEditorProperties{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", elements=" + elements +
            '}';
    }
}

package com.fourigin.argo.models.action;

import com.fourigin.argo.models.content.elements.ContentElement;

import java.io.Serializable;
import java.util.Objects;

public class ContentContainer implements Serializable {
    private static final long serialVersionUID = -9218481462056772059L;

    private ContentReference contentReference;
    private ContentElement contentElement;

    public ContentReference getContentReference() {
        return contentReference;
    }

    public void setContentReference(ContentReference contentReference) {
        this.contentReference = contentReference;
    }

    public ContentElement getContentElement() {
        return contentElement;
    }

    public void setContentElement(ContentElement contentElement) {
        this.contentElement = contentElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentContainer that = (ContentContainer) o;
        return Objects.equals(contentReference, that.contentReference) &&
                Objects.equals(contentElement, that.contentElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentReference, contentElement);
    }

    @Override
    public String toString() {
        return "ContentContainer{" +
                "contentReference=" + contentReference +
                ", contentElement=" + contentElement +
                '}';
    }
}

package com.fourigin.argo.models.content.elements;

import java.util.Objects;

public abstract class AbstractContentElement extends AbstractAttributesAwareContentElement implements ContentElement {

    private static final long serialVersionUID = 2481851091542511335L;

    private String name;
    private String title;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractContentElement)) return false;
        if (!super.equals(o)) return false;
        AbstractContentElement that = (AbstractContentElement) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, title);
    }
}

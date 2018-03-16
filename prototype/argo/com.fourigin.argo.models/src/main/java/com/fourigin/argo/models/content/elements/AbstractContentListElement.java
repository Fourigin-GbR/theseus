package com.fourigin.argo.models.content.elements;

import java.util.Objects;

public abstract class AbstractContentListElement extends AbstractAttributesAwareContentElement implements ContentListElement {

    private static final long serialVersionUID = 6898701711360826186L;

    private String title;

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
        if (!(o instanceof AbstractContentListElement)) return false;
        if (!super.equals(o)) return false;
        AbstractContentListElement that = (AbstractContentListElement) o;
        return Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title);
    }
}

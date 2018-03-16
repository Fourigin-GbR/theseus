package com.fourigin.argo.models.content.hotspots;

import java.io.Serializable;
import java.util.Objects;

public class DefaultElementEditorProperties implements Serializable, ElementEditorProperties {
    private static final long serialVersionUID = -8724298379198572167L;

    private String title;

    private String description;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultElementEditorProperties)) return false;
        DefaultElementEditorProperties that = (DefaultElementEditorProperties) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }

    @Override
    public String toString() {
        return "DefaultElementEditorProperties{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}

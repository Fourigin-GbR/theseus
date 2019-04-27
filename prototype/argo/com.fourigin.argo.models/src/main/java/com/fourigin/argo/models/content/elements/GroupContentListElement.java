package com.fourigin.argo.models.content.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GroupContentListElement extends AbstractAttributesAwareContentElement implements ListElement, ContentElementsContainer {

    private static final long serialVersionUID = -1026281367445257627L;

    private LanguageContent title;

    private List<ContentElement> elements;

    public LanguageContent getTitle() {
        return title;
    }

    public void setTitle(LanguageContent title) {
        this.title = title;
    }

    public List<ContentElement> getElements() {
        return elements;
    }

    public void setElements(List<ContentElement> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupContentListElement)) return false;
        GroupContentListElement that = (GroupContentListElement) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, elements);
    }

    @Override
    public String toString() {
        return "GroupContentListElement{" +
            "title='" + title + '\'' +
            ", elements=" + elements +
            '}';
    }

    public static class Builder {
        private LanguageContent title = new LanguageContent();
        private List<ContentElement> elements = new ArrayList<>();

        public Builder withTitle(String language, String title) {
            this.title.put(language, title);
            return this;
        }

        public Builder withElement(ContentElement element) {
            if (element != null) {
                elements.add(element);
            }

            return this;
        }

        public Builder withElements(ContentElement... elements) {
            if (elements != null) {
                this.elements.addAll(Arrays.asList(elements));
            }

            return this;
        }

        public Builder withElements(List<ContentElement> elements) {
            if (elements != null) {
                this.elements.addAll(elements);
            }

            return this;
        }

        public GroupContentListElement build() {
            GroupContentListElement group = new GroupContentListElement();
            if (!title.isEmpty()) {
                group.setTitle(title);
            }
            group.setElements(elements);
            return group;
        }
    }
}

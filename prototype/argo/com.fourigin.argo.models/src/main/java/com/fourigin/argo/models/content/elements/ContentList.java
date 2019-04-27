package com.fourigin.argo.models.content.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ContentList extends AbstractContentElement implements NamedElement, ContentListElementsContainer {

    private static final long serialVersionUID = 1256571424877498311L;

    private LanguageContent title;
    private List<ListElement> elements;

    @Override
    public LanguageContent getTitle() {
        return title;
    }

    @Override
    public void setTitle(LanguageContent title) {
        this.title = title;
    }

    public List<ListElement> getElements() {
        return elements;
    }

    public void setElements(List<ListElement> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentList)) return false;
        if (!super.equals(o)) return false;
        ContentList that = (ContentList) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, elements);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ContentList{");

        builder.append("name='").append(getName()).append('\'');

        LanguageContent title = getTitle();
        if (title != null) {
            builder.append(", title='").append(title).append('\'');
        }

        Map<String, String> attributes = getAttributes();
        if (attributes != null) {
            builder.append(", attributes='").append(attributes).append('\'');
        }

        builder.append(", elements='").append(elements).append('\'');

        return builder.toString();
    }

    public static class Builder {
        private String name;
        private LanguageContent title = new LanguageContent();
        private List<ListElement> elements = new ArrayList<>();
        private Map<String, String> attributes = new HashMap<>();

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withTitle(String language, String title) {
            this.title.put(language, title);
            return this;
        }

        public Builder withElement(ListElement element) {
            if (element != null) {
                elements.add(element);
            }

            return this;
        }

        public Builder withElements(ListElement... elements) {
            if (elements != null) {
                this.elements.addAll(Arrays.asList(elements));
            }

            return this;
        }

        public Builder withElements(List<ListElement> elements) {
            if (elements != null) {
                this.elements.addAll(elements);
            }

            return this;
        }

        public Builder withAttribute(String key, String value) {
            if (key != null) {
                if (value == null) {
                    this.attributes.remove(key);
                } else {
                    this.attributes.put(key, value);
                }
            }

            return this;
        }

        public ContentList build() {
            ContentList list = new ContentList();
            list.setName(name);
            if (!title.isEmpty()) {
                list.setTitle(title);
            }
            list.setElements(elements);
            if (!attributes.isEmpty()) {
                list.setAttributes(attributes);
            }
            return list;
        }
    }
}

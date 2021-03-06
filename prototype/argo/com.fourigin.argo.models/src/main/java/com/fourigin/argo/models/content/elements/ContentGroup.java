package com.fourigin.argo.models.content.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ContentGroup extends AbstractContentElement implements ContentElement, ContentElementsContainer {
    private static final long serialVersionUID = -6891589536053329842L;

    private LanguageContent title;
    private List<ContentElement> elements;

    @Override
    public LanguageContent getTitle() {
        return title;
    }

    @Override
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
        if (!(o instanceof ContentGroup)) return false;
        if (!super.equals(o)) return false;
        ContentGroup that = (ContentGroup) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, elements);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ContentGroup{");

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
        private List<ContentElement> elements = new ArrayList<>();
        private Map<String, String> attributes = new HashMap<>();

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

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

        public ContentGroup build() {
            ContentGroup group = new ContentGroup();
            group.setName(name);
            if (!title.isEmpty()) {
                group.setTitle(title);
            }
            if (!elements.isEmpty()) {
                group.setElements(elements);
            }
            if (!attributes.isEmpty()) {
                group.setAttributes(attributes);
            }
            return group;
        }
    }
}

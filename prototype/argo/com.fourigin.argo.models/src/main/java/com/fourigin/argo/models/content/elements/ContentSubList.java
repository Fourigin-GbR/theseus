package com.fourigin.argo.models.content.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ContentSubList extends AbstractAttributesAwareContentElement implements ContentListElement, TitleAwareContentElement, ContentElementsContainer<ContentListElement> {

    private static final long serialVersionUID = 1256571424877498311L;

    private LanguageContent title;
    private List<ContentListElement> elements;

    @Override
    public LanguageContent getTitle() {
        return title;
    }

    @Override
    public void setTitle(LanguageContent title) {
        this.title = title;
    }

    public List<ContentListElement> getElements() {
        return elements;
    }

    public void setElements(List<ContentListElement> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentSubList)) return false;
        if (!super.equals(o)) return false;
        ContentSubList that = (ContentSubList) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, elements);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ContentSubList{");

        LanguageContent title = getTitle();
        if (title != null) {
            builder.append("title='").append(title).append('\'');
        }

        Map<String, String> attributes = getAttributes();
        if (attributes != null) {
            builder.append(", attributes='").append(attributes).append('\'');
        }

        builder.append(", elements='").append(elements).append('\'');

        return builder.toString();
    }

    public static class Builder {
        private LanguageContent title = new LanguageContent();
        private List<ContentListElement> elements = new ArrayList<>();
        private Map<String, String> attributes = new HashMap<>();

        public Builder withTitle(String language, String title) {
            this.title.put(language, title);
            return this;
        }

        public Builder withElement(ContentListElement element) {
            if (element != null) {
                elements.add(element);
            }

            return this;
        }

        public Builder withElements(ContentListElement... elements) {
            if (elements != null) {
                this.elements.addAll(Arrays.asList(elements));
            }

            return this;
        }

        public Builder withElements(List<ContentListElement> elements) {
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

        public ContentSubList build() {
            ContentSubList list = new ContentSubList();
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

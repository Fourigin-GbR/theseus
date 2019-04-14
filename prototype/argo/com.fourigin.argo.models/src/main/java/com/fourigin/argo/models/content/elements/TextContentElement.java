package com.fourigin.argo.models.content.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TextContentElement extends AbstractContentElement implements TextAwareContentElement, ContentElement {
    private static final long serialVersionUID = 5066464546311137699L;

    private LanguageContent title;
    private LanguageContent content;

    @Override
    public LanguageContent getTitle() {
        return title;
    }

    @Override
    public void setTitle(LanguageContent title) {
        this.title = title;
    }

    public LanguageContent getContent() {
        return content;
    }

    public void setContent(LanguageContent content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextContentElement)) return false;
        if (!super.equals(o)) return false;
        TextContentElement that = (TextContentElement) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), content);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TextContentElement{");

        builder.append("name='").append(getName()).append('\'');

        LanguageContent title = getTitle();
        if (title != null) {
            builder.append(", title='").append(title).append('\'');
        }

        builder.append(", content='").append(content).append('\'');

        Map<String, String> attributes = getAttributes();
        if (attributes != null) {
            builder.append(", attributes='").append(attributes).append('\'');
        }

        builder.append('}');

        return builder.toString();
    }

    public static class Builder {
        private String name;
        private LanguageContent title = new LanguageContent();
        private LanguageContent content = new LanguageContent();
        private Map<String, String> attributes = new HashMap<>();

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withTitle(String language, String title) {
            this.title.put(language, title);
            return this;
        }

        public Builder withContent(String context, String content) {
            this.content.put(context, content);
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

        public TextContentElement build() {
            TextContentElement element = new TextContentElement();
            element.setName(name);
            if (!title.isEmpty()) {
                element.setTitle(title);
            }
            element.setContent(content);
            if (!attributes.isEmpty()) {
                element.setAttributes(attributes);
            }

            return element;
        }
    }
}

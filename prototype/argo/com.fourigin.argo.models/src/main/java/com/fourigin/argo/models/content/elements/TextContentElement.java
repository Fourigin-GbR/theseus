package com.fourigin.argo.models.content.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TextContentElement extends AbstractContentElement implements TextAwareContentElement, ContentElement {
    private static final long serialVersionUID = 5066464546311137699L;

    private String content;
    private Map<String, String> contextSpecificContent;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getContextSpecificContent() {
        return contextSpecificContent;
    }

    public void setContextSpecificContent(Map<String, String> content){
        contextSpecificContent = content;
    }

    public String getContextSpecificContent(String context, boolean fallback) {
        if (contextSpecificContent == null || contextSpecificContent.isEmpty()) {
            return fallback ? content : null;
        }

        if (!contextSpecificContent.containsKey(context)) {
            return fallback ? content : null;
        }

        return contextSpecificContent.get(context);
    }

    public void setContextSpecificContent(String context, String content) {
        Objects.requireNonNull(context, "context must not be null!");

        if (content == null) {
            if (contextSpecificContent == null || contextSpecificContent.isEmpty()) {
                return;
            }

            contextSpecificContent.remove(context);
            return;
        }

        if (contextSpecificContent == null) {
            contextSpecificContent = new HashMap<>();
        }
        contextSpecificContent.put(context, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextContentElement)) return false;
        if (!super.equals(o)) return false;
        TextContentElement that = (TextContentElement) o;
        return Objects.equals(content, that.content) &&
            Objects.equals(contextSpecificContent, that.contextSpecificContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), content, contextSpecificContent);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TextContentElement{");

        builder.append("name='").append(getName()).append('\'');

        String title = getTitle();
        if(title != null) {
            builder.append(", title='").append(title).append('\'');
        }

        builder.append(", content='").append(content).append('\'');

        if (contextSpecificContent != null) {
            builder.append(", contextSpecificContent=").append(contextSpecificContent);
        }

        Map<String, String> attributes = getAttributes();
        if (attributes != null) {
            builder.append(", attributes='").append(attributes).append('\'');
        }

        builder.append('}');

        return builder.toString();
    }

    public static class Builder {
        private String name;
        private String title;
        private String content;
        private Map<String, String> contextSpecificContent = new HashMap<>();
        private Map<String, String> attributes = new HashMap<>();

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withContextSpecificContent(String context, String content) {
            contextSpecificContent.put(context, content);
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
            element.setTitle(title);
            element.setContent(content);
            if (!contextSpecificContent.isEmpty()) {
                element.setContextSpecificContent(contextSpecificContent);
            }
            if (!attributes.isEmpty()) {
                element.setAttributes(attributes);
            }

            return element;
        }
    }
}

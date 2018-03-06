package com.fourigin.argo.models.content.elements.list;

import com.fourigin.argo.models.content.elements.TextAwareContentElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TextContentListElement extends AbstractContentListElement implements TextAwareContentElement, ContentListElement {

    private static final long serialVersionUID = 4912756036611660964L;

    private String content;
    private Map<String, String> contextSpecificContent;

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getContextSpecificContent(String context, boolean fallback) {
        if (contextSpecificContent == null || contextSpecificContent.isEmpty()) {
            return fallback ? content : null;
        }

        if (!contextSpecificContent.containsKey(context)) {
            return fallback ? content : null;
        }

        return contextSpecificContent.get(context);
    }

    @Override
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

    public void setContextSpecificContent(Map<String, String> content){
        contextSpecificContent = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextContentListElement)) return false;
        if (!super.equals(o)) return false;
        TextContentListElement that = (TextContentListElement) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), content);
    }

    @Override
    public String toString() {
        return "TextContentListElement{" +
            "content='" + content + '\'' +
            '}';
    }

    public static class Builder {
        private String title;
        private String content;
        private Map<String, String> contextSpecificContent = new HashMap<>();
        private Map<String, String> attributes = new HashMap<>();

        public Builder withTitle(String title){
            this.title = title;
            return this;
        }

        public Builder withContent(String content){
            this.content = content;
            return this;
        }

        public TextContentListElement.Builder withContextSpecificContent(String context, String content) {
            contextSpecificContent.put(context, content);
            return this;
        }

        public TextContentListElement.Builder withAttribute(String key, String value) {
            if (key != null) {
                if (value == null) {
                    this.attributes.remove(key);
                } else {
                    this.attributes.put(key, value);
                }
            }

            return this;
        }

        public TextContentListElement build(){
            TextContentListElement element = new TextContentListElement();
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

package com.fourigin.argo.models.content.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TextContentListElement extends AbstractAttributesAwareContentElement implements TextAwareContentElement, TitleAwareContentElement, ListElement {

    private static final long serialVersionUID = 4912756036611660964L;

    private LanguageContent title;
    private LanguageContent content;

    @Override
    public LanguageContent getTitle() {
        return title;
    }

    public void setTitle(LanguageContent title) {
        this.title = title;
    }

    @Override
    public LanguageContent getContent() {
        return content;
    }

    @Override
    public void setContent(LanguageContent content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextContentListElement)) return false;
        TextContentListElement that = (TextContentListElement) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content);
    }

    @Override
    public String toString() {
        return "TextContentListElement{" +
            "content='" + content + '\'' +
            '}';
    }

    public static class Builder {
        private LanguageContent title = new LanguageContent();
        private LanguageContent content = new LanguageContent();
        private Map<String, String> attributes = new HashMap<>();

        public Builder withTitle(String language, String title){
            this.title.put(language, title);
            return this;
        }

        public Builder withContent(String language, String content){
            this.content.put(language, content);
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

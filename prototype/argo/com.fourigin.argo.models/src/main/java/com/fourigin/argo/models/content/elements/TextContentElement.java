package com.fourigin.argo.models.content.elements;

import java.util.HashMap;
import java.util.Map;

public class TextContentElement extends AbstractContentElement implements TextAwareContentElement, ContentElement {
    private static final long serialVersionUID = 5066464546311137699L;

    private String content;
    private boolean markupAllowed;

    private static final boolean DEFAULT_MARKUP_ALLOWED = false;

    public TextContentElement(){
        this(DEFAULT_MARKUP_ALLOWED);
    }

    public TextContentElement(boolean markupAllowed){
        this.setMarkupAllowed(markupAllowed);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMarkupAllowed() {
        return markupAllowed;
    }

    public void setMarkupAllowed(boolean markupAllowed) {
        this.markupAllowed = markupAllowed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextContentElement)) return false;
        if (!super.equals(o)) return false;

        TextContentElement that = (TextContentElement) o;

        //noinspection SimplifiableIfStatement
        if (markupAllowed != that.markupAllowed) return false;
        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (markupAllowed ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TextContentElement{" +
          "name='" + getName() + '\'' +
          ", title='" + getTitle() + '\'' +
          ", content='" + content + '\'' +
          ", markupAllowed=" + markupAllowed +
          '}';
    }

    public static class Builder {
        private String name;
        private String title;
        private String content;
        private boolean markupAllowed = DEFAULT_MARKUP_ALLOWED;
        private Map<String, String> attributes = new HashMap<>();

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withTitle(String title){
            this.title = title;
            return this;
        }

        public Builder withContent(String content){
            this.content = content;
            return this;
        }

        public Builder withMarkupAllowed(boolean markupAllowed){
            this.markupAllowed = markupAllowed;
            return this;
        }

        public Builder withAttribute(String key, String value){
            if(key != null) {
                if (value == null) {
                    this.attributes.remove(key);
                } else {
                    this.attributes.put(key, value);
                }
            }

            return this;
        }

        public TextContentElement build(){
            TextContentElement element = new TextContentElement();
            element.setName(name);
            element.setTitle(title);
            element.setContent(content);
            element.setMarkupAllowed(markupAllowed);
            if(!attributes.isEmpty()){
                element.setAttributes(attributes);
            }

            return element;
        }
    }
}
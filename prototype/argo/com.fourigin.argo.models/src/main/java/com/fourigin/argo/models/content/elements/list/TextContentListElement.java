package com.fourigin.argo.models.content.elements.list;

import com.fourigin.argo.models.content.elements.TextAwareContentElement;

public class TextContentListElement implements TextAwareContentElement, ContentListElement {

    private static final long serialVersionUID = 4912756036611660964L;

    private String title;
    private String content;
    private boolean markupAllowed;

    private static final boolean DEFAULT_MARKUP_ALLOWED = false;

    public TextContentListElement(){
        this(DEFAULT_MARKUP_ALLOWED);
    }

    public TextContentListElement(boolean markupAllowed){
        this.setMarkupAllowed(markupAllowed);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        if (!(o instanceof TextContentListElement)) return false;

        TextContentListElement that = (TextContentListElement) o;

        if (markupAllowed != that.markupAllowed) return false;
        //noinspection SimplifiableIfStatement
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (markupAllowed ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TextContentListElement{" +
            "title='" + title + '\'' +
            ", content='" + content + '\'' +
            ", markupAllowed=" + markupAllowed +
            '}';
    }

    public static class Builder {
        private String title;
        private String content;
        private boolean markupAllowed = DEFAULT_MARKUP_ALLOWED;

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder markupAllowed(boolean markupAllowed){
            this.markupAllowed = markupAllowed;
            return this;
        }

        public TextContentListElement build(){
            TextContentListElement element = new TextContentListElement();
            element.setTitle(title);
            element.setContent(content);
            element.setMarkupAllowed(markupAllowed);
            return element;
        }
    }
}
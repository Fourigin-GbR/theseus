package com.fourigin.argo.models.content.elements;

public class TextLinkContentElement extends AbstractContentElement implements TextAwareContentElement, LinkAwareContentElement, ContentElement {
    private static final long serialVersionUID = 8683724707926036886L;

    private String content;
    private boolean markupAllowed;
    private String url;
    private String anchorName;
    private String target;

    private static final boolean DEFAULT_MARKUP_ALLOWED = false;

    public TextLinkContentElement(){
        this(DEFAULT_MARKUP_ALLOWED);
    }

    public TextLinkContentElement(boolean markupAllowed){
        this.markupAllowed = markupAllowed;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextLinkContentElement)) return false;
        if (!super.equals(o)) return false;

        TextLinkContentElement that = (TextLinkContentElement) o;

        if (markupAllowed != that.markupAllowed) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        //noinspection SimplifiableIfStatement
        if (anchorName != null ? !anchorName.equals(that.anchorName) : that.anchorName != null) return false;
        return target != null ? target.equals(that.target) : that.target == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (markupAllowed ? 1 : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (anchorName != null ? anchorName.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TextLinkContentElement{" +
          "name='" + getName() + '\'' +
          ", title='" + getTitle() + '\'' +
          ", content='" + content + '\'' +
          ", markupAllowed=" + markupAllowed +
          ", url='" + url + '\'' +
          ", anchorName='" + anchorName + '\'' +
          ", target='" + target + '\'' +
          '}';
    }

    public static class Builder {
        private String name;
        private String title;
        private String content;
        private boolean markupAllowed = DEFAULT_MARKUP_ALLOWED;
        private String url;
        private String anchorName;
        private String target;

        public Builder name(String name){
            this.name = name;
            return this;
        }

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

        public Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder anchorName(String anchorName){
            this.anchorName = anchorName;
            return this;
        }

        public Builder target(String target){
            this.target = target;
            return this;
        }

        public TextLinkContentElement build(){
            TextLinkContentElement element = new TextLinkContentElement();
            element.setName(name);
            element.setTitle(title);
            element.setContent(content);
            element.setMarkupAllowed(markupAllowed);
            element.setUrl(url);
            element.setAnchorName(anchorName);
            element.setTarget(target);
            return element;
        }
    }
}

package com.fourigin.argo.models.content.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LinkListElement extends AbstractAttributesAwareContentElement implements ContentListElement, ContentElementsContainer<ContentElement>, LinkAwareContentElement {

    private static final long serialVersionUID = -5241678038301475481L;

    private LanguageContent title;
    private List<ContentElement> elements;
    private String url;
    private String anchorName;
    private String target;

    public LanguageContent getTitle() {
        return title;
    }

    public void setTitle(LanguageContent title) {
        this.title = title;
    }

    @Override
    public List<ContentElement> getElements() {
        return elements;
    }

    @Override
    public void setElements(List<ContentElement> elements) {
        this.elements = elements;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getAnchorName() {
        return anchorName;
    }

    @Override
    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinkListElement)) return false;
        LinkListElement that = (LinkListElement) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(elements, that.elements) &&
            Objects.equals(url, that.url) &&
            Objects.equals(anchorName, that.anchorName) &&
            Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, elements, url, anchorName, target);
    }

    @Override
    public String toString() {
        return "LinkListElement{" +
            "title=" + title +
            ", elements=" + elements +
            ", url='" + url + '\'' +
            ", anchorName='" + anchorName + '\'' +
            ", target='" + target + '\'' +
            '}';
    }

    public static class Builder {
        private LanguageContent title = new LanguageContent();
        private String url;
        private String anchorName;
        private String target;
        private Map<String, String> attributes = new HashMap<>();

        public Builder withTitle(String language, String title) {
            this.title.put(language, title);
            return this;
        }

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withAnchorName(String anchorName) {
            this.anchorName = anchorName;
            return this;
        }

        public Builder withTarget(String target) {
            this.target = target;
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

        public LinkListElement build() {
            LinkListElement element = new LinkListElement();
            if (!title.isEmpty()) {
                element.setTitle(title);
            }
            element.setUrl(url);
            element.setAnchorName(anchorName);
            element.setTarget(target);
            if (!attributes.isEmpty()) {
                element.setAttributes(attributes);
            }
            return element;
        }
    }
}

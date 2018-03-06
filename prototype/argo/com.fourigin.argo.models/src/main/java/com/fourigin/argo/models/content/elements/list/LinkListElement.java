package com.fourigin.argo.models.content.elements.list;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.ContentElementsContainer;
import com.fourigin.argo.models.content.elements.LinkAwareContentElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LinkListElement extends AbstractContentListElement implements ContentListElement, ContentElementsContainer, LinkAwareContentElement{

    private static final long serialVersionUID = -5241678038301475481L;

    private List<ContentElement> elements;
    private String url;
    private String anchorName;
    private String target;

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
        if (!super.equals(o)) return false;
        LinkListElement that = (LinkListElement) o;
        return Objects.equals(elements, that.elements) &&
            Objects.equals(url, that.url) &&
            Objects.equals(anchorName, that.anchorName) &&
            Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elements, url, anchorName, target);
    }

    @Override
    public String toString() {
        return "LinkElement{" +
            "elements=" + elements +
            ", url='" + url + '\'' +
            ", anchorName='" + anchorName + '\'' +
            ", target='" + target + '\'' +
            '}';
    }

    public static class Builder {
        private String title;
        private String url;
        private String anchorName;
        private String target;
        private Map<String, String> attributes = new HashMap<>();

        public LinkListElement.Builder withTitle(String title){
            this.title = title;
            return this;
        }

        public LinkListElement.Builder withUrl(String url){
            this.url = url;
            return this;
        }

        public LinkListElement.Builder withAnchorName(String anchorName){
            this.anchorName = anchorName;
            return this;
        }

        public LinkListElement.Builder withTarget(String target){
            this.target = target;
            return this;
        }

        public LinkListElement.Builder withAttribute(String key, String value){
            if(key != null) {
                if (value == null) {
                    this.attributes.remove(key);
                } else {
                    this.attributes.put(key, value);
                }
            }

            return this;
        }

        public LinkListElement build(){
            LinkListElement element = new LinkListElement();
            element.setTitle(title);
            element.setUrl(url);
            element.setAnchorName(anchorName);
            element.setTarget(target);
            if(!attributes.isEmpty()){
                element.setAttributes(attributes);
            }
            return element;
        }
    }
}

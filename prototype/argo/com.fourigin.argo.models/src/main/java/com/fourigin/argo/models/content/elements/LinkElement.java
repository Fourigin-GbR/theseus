package com.fourigin.argo.models.content.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LinkElement extends AbstractContentElement implements ContentElement, ContentElementsContainer<ContentElement>, LinkAwareContentElement{

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
        if (!(o instanceof LinkElement)) return false;
        if (!super.equals(o)) return false;
        LinkElement that = (LinkElement) o;
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
        StringBuilder builder = new StringBuilder("LinkElement{");

        builder.append("name='").append(getName()).append('\'');

        String title = getTitle();
        if(title != null){
            builder.append(", title='").append(title).append('\'');
        }

        builder.append(", url='").append(url).append('\'');

        if(anchorName != null){
            builder.append(", anchorName='").append(anchorName).append('\'');
        }

        if(target != null){
            builder.append(", target='").append(target).append('\'');
        }

        Map<String, String> attributes = getAttributes();
        if (attributes != null) {
            builder.append(", attributes='").append(attributes).append('\'');
        }

        builder.append(", elements='").append(elements).append('\'');

        return builder.toString();
    }

    public static class Builder {
        private String name;
        private String title;
        private String url;
        private String anchorName;
        private String target;
        private List<ContentElement> elements = new ArrayList<>();
        private Map<String, String> attributes = new HashMap<>();

        public LinkElement.Builder withName(String name){
            this.name = name;
            return this;
        }

        public LinkElement.Builder withTitle(String title){
            this.title = title;
            return this;
        }

        public LinkElement.Builder withUrl(String url){
            this.url = url;
            return this;
        }

        public LinkElement.Builder withAnchorName(String anchorName){
            this.anchorName = anchorName;
            return this;
        }

        public LinkElement.Builder withTarget(String target){
            this.target = target;
            return this;
        }

        public LinkElement.Builder withElement(ContentElement element){
            if(element != null) {
                elements.add(element);
            }

            return this;
        }

        public LinkElement.Builder withElements(ContentElement... elements){
            if(elements != null){
                this.elements.addAll(Arrays.asList(elements));
            }

            return this;
        }

        public LinkElement.Builder withElements(List<ContentElement> elements){
            if(elements != null){
                this.elements.addAll(elements);
            }

            return this;
        }

        public LinkElement.Builder withAttribute(String key, String value){
            if(key != null) {
                if (value == null) {
                    this.attributes.remove(key);
                } else {
                    this.attributes.put(key, value);
                }
            }

            return this;
        }

        public LinkElement build(){
            LinkElement element = new LinkElement();
            element.setName(name);
            element.setTitle(title);
            element.setUrl(url);
            element.setAnchorName(anchorName);
            element.setTarget(target);
            if(!elements.isEmpty()) {
                element.setElements(elements);
            }
            if(!attributes.isEmpty()){
                element.setAttributes(attributes);
            }
            return element;
        }
    }
}

package com.fourigin.argo.models.content.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentGroup extends AbstractContentElement implements ContentElement, ContentElementsContainer<ContentElement> {
    private static final long serialVersionUID = -6891589536053329842L;

    private List<ContentElement> elements;

    public List<ContentElement> getElements() {
        return elements;
    }

    public void setElements(List<ContentElement> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentGroup)) return false;
        if (!super.equals(o)) return false;

        ContentGroup that = (ContentGroup) o;

        return elements != null ? elements.equals(that.elements) : that.elements == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (elements != null ? elements.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ContentGroup{");

        builder.append("name='").append(getName()).append('\'');

        String title = getTitle();
        if(title != null){
            builder.append(", title='").append(title).append('\'');
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
        private List<ContentElement> elements = new ArrayList<>();
        private Map<String, String> attributes = new HashMap<>();

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withElement(ContentElement element){
            if(element != null) {
                elements.add(element);
            }

            return this;
        }

        public Builder withElements(ContentElement... elements){
            if(elements != null){
                this.elements.addAll(Arrays.asList(elements));
            }

            return this;
        }

        public Builder withElements(List<ContentElement> elements){
            if(elements != null){
                this.elements.addAll(elements);
            }

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

        public ContentGroup build(){
            ContentGroup group = new ContentGroup();
            group.setName(name);
            if(!elements.isEmpty()) {
                group.setElements(elements);
            }
            if(!attributes.isEmpty()){
                group.setAttributes(attributes);
            }
            return group;
        }
    }
}

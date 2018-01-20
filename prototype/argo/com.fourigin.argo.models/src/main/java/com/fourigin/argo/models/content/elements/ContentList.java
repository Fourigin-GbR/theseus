package com.fourigin.argo.models.content.elements;

import com.fourigin.argo.models.content.elements.list.ContentListElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentList extends AbstractContentElement implements ContentElement {

    private static final long serialVersionUID = 1256571424877498311L;

    private List<ContentListElement> elements;

    public List<ContentListElement> getElements() {
        return elements;
    }

    public void setElements(List<ContentListElement> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentList)) return false;
        if (!super.equals(o)) return false;

        ContentList that = (ContentList) o;

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
        return "ContentList{" +
          "elements=" + elements +
          '}';
    }

    public static class Builder {
        private String name;
        private List<ContentListElement> elements = new ArrayList<>();
        private Map<String, String> attributes = new HashMap<>();

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withElement(ContentListElement element){
            if(element != null) {
                elements.add(element);
            }

            return this;
        }

        public Builder withElements(ContentListElement... elements){
            if(elements != null){
                this.elements.addAll(Arrays.asList(elements));
            }

            return this;
        }

        public Builder withElements(List<ContentListElement> elements){
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


        public ContentList build(){
            ContentList group = new ContentList();
            group.setName(name);
            group.setElements(elements);
            if(!attributes.isEmpty()){
                group.setAttributes(attributes);
            }
            return group;
        }

    }
}
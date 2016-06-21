package com.fourigin.cms.models.content.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContentGroup extends AbstractContentElement implements ContentElement {
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
        return "ContentGroup{" +
          "elements=" + elements +
          '}';
    }

    public static class Builder {
        private List<ContentElement> elements = new ArrayList<>();

        public Builder element(ContentElement element){
            if(element != null) {
                elements.add(element);
            }

            return this;
        }

        public Builder elements(ContentElement... elements){
            if(elements != null){
                this.elements.addAll(Arrays.asList(elements));
            }

            return this;
        }

        public Builder elements(List<ContentElement> elements){
            if(elements != null){
                this.elements.addAll(elements);
            }

            return this;
        }

        public ContentGroup build(){
            ContentGroup group = new ContentGroup();
            group.setElements(elements);
            return group;
        }

    }
}
package com.fourigin.argo.models.content.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupContentListElement extends AbstractContentListElement implements ContentListElement, ContentElementsContainer<ContentElement> {

    private static final long serialVersionUID = -1026281367445257627L;

    private String title;

    private List<ContentElement> elements;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ContentElement> getElements() {
        return elements;
    }

    public void setElements(List<ContentElement> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupContentListElement)) return false;

        GroupContentListElement that = (GroupContentListElement) o;

        //noinspection SimplifiableIfStatement
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return elements != null ? elements.equals(that.elements) : that.elements == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (elements != null ? elements.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GroupContentListElement{" +
            "title='" + title + '\'' +
            ", elements=" + elements +
            '}';
    }

    public static class Builder {
        private String title;
        private List<ContentElement> elements = new ArrayList<>();

        public Builder withTitle(String title){
            this.title = title;
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

        public GroupContentListElement build(){
            GroupContentListElement group = new GroupContentListElement();
            group.setTitle(title);
            group.setElements(elements);
            return group;
        }
    }
}

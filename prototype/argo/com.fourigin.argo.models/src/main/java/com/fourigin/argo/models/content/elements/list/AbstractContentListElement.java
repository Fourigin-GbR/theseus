package com.fourigin.argo.models.content.elements.list;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractContentListElement implements ContentListElement {

    private static final long serialVersionUID = 6898701711360826186L;

    private String title;
    private Map<String, String> attributes;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void setAttribute(String key, String value){
        if(key == null){
            return;
        }

        if(attributes == null){
            if(value == null){
                return;
            }

            attributes = new HashMap<>();
        }
        else {
            if(value == null){
                attributes.remove(key);
            }
        }

        attributes.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractContentListElement)) return false;
        AbstractContentListElement that = (AbstractContentListElement) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, attributes);
    }
}

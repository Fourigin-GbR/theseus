package com.fourigin.argo.models.content.elements;

import java.util.HashMap;
import java.util.Map;

abstract public class AbstractContentElement implements ContentElement {
    private static final long serialVersionUID = 2481851091542511335L;

    private String name;
    private String title;
    private Map<String, String> attributes;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

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
        if (!(o instanceof AbstractContentElement)) return false;

        AbstractContentElement that = (AbstractContentElement) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        //noinspection SimplifiableIfStatement
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return attributes != null ? attributes.equals(that.attributes) : that.attributes == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }
}

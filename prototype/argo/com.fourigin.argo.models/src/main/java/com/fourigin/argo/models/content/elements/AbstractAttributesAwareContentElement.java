package com.fourigin.argo.models.content.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractAttributesAwareContentElement implements AttributesAwareContentElement {

    private static final long serialVersionUID = -4091011146881834979L;

    private Map<String, String> attributes;
    private Map<String, Object> typeProperties;

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
        setMapValue(key, value, attributes);
    }

    @Override
    public Map<String, Object> getTypeProperties() {
        return typeProperties;
    }

    @Override
    public void setTypeProperties(Map<String, Object> typeProperties) {
        this.typeProperties = typeProperties;
    }

    @Override
    public void setTypeProperty(String key, Object value){
        setMapValue(key, value, typeProperties);
    }

    private static <T> void setMapValue(String key, T value, Map<String, T> map){
        if(key == null){
            return;
        }

        if(map == null){
            if(value == null){
                return;
            }

            map = new HashMap<>();
        }
        else {
            if(value == null){
                map.remove(key);
            }
        }

        map.put(key, value);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractAttributesAwareContentElement)) return false;
        AbstractAttributesAwareContentElement that = (AbstractAttributesAwareContentElement) o;
        return Objects.equals(attributes, that.attributes) &&
            Objects.equals(typeProperties, that.typeProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, typeProperties);
    }
}

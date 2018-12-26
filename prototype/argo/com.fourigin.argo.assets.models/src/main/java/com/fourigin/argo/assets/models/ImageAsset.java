package com.fourigin.argo.assets.models;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ImageAsset implements Asset {
    private String id;
    private String name;
    private String mimeType;
    private Set<String> tags;
    private Map<String, String> attributes;
    private Dimension size;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public Set<String> getTags() {
        return tags;
    }

    @Override
    public void setTags(Set<String> tags) {
        this.tags = new HashSet<>(tags);
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = new HashMap<>(attributes);
    }

    @Override
    public String getAttribute(String key) {
        if (attributes == null) {
            return null;
        }

        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, String value) {
        if (key == null) {
            return;
        }

        if(attributes == null){
            attributes = new HashMap<>();
        }

        if(value == null){
            attributes.remove(key);

            if(attributes.isEmpty()){
                attributes = null;
            }
        }
        else {
            attributes.put(key, value);
        }
    }

    @Override
    public void removeAttribute(String key) {
        if(attributes == null || attributes.isEmpty()){
            return;
        }

        attributes.remove(key);

        if(attributes.isEmpty()){
            attributes = null;
        }
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public void setSize(int width, int height){
        this.size = new Dimension(width, height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageAsset)) return false;
        ImageAsset that = (ImageAsset) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(mimeType, that.mimeType) &&
            Objects.equals(tags, that.tags) &&
            Objects.equals(attributes, that.attributes) &&
            Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, mimeType, tags, attributes, size);
    }

    @Override
    public String toString() {
        return "ImageAsset{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", mimeType='" + mimeType + '\'' +
            ", tags=" + tags +
            ", attributes=" + attributes +
            ", size=" + size +
            '}';
    }
}

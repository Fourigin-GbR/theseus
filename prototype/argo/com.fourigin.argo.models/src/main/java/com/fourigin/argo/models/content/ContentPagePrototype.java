package com.fourigin.argo.models.content;

import com.fourigin.argo.models.content.hotspots.ElementsEditorProperties;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class ContentPagePrototype implements Serializable {

    private static final long serialVersionUID = -3029365377046552324L;

    private ContentPage contentPrototype;
    private Map<String, ElementsEditorProperties> hotspots;

    public ContentPage getContentPrototype() {
        return contentPrototype;
    }

    public void setContentPrototype(ContentPage contentPrototype) {
        this.contentPrototype = contentPrototype;
    }

    public Map<String, ElementsEditorProperties> getHotspots() {
        return hotspots;
    }

    public void setHotspots(Map<String, ElementsEditorProperties> hotspots) {
        this.hotspots = hotspots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentPagePrototype)) return false;
        ContentPagePrototype that = (ContentPagePrototype) o;
        return Objects.equals(contentPrototype, that.contentPrototype) &&
            Objects.equals(hotspots, that.hotspots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentPrototype, hotspots);
    }

    @Override
    public String toString() {
        return "ContentPagePrototype{" +
            "contentPrototype=" + contentPrototype +
            ", hotspots=" + hotspots +
            '}';
    }
}

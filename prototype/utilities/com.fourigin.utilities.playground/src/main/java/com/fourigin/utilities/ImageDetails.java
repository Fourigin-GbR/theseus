package com.fourigin.utilities;

import java.util.Objects;

public class ImageDetails {
    private String id;
    private String alternateText;

    public ImageDetails() {
    }

    public ImageDetails(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlternateText() {
        return alternateText;
    }

    public void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageDetails)) return false;
        ImageDetails that = (ImageDetails) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(alternateText, that.alternateText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, alternateText);
    }

    @Override
    public String toString() {
        return "ImageDetails{" +
            "id='" + id + '\'' +
            ", alternateText='" + alternateText + '\'' +
            '}';
    }
}

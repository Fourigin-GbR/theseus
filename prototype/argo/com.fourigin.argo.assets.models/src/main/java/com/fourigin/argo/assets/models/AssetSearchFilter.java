package com.fourigin.argo.assets.models;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AssetSearchFilter implements Serializable {

    private static final long serialVersionUID = 9016362333449795142L;

    private Integer width;
    private Integer height;
    private String mimeType;
    private Set<String> tags;
    private String keyword;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssetSearchFilter)) return false;
        AssetSearchFilter that = (AssetSearchFilter) o;
        return Objects.equals(width, that.width) &&
            Objects.equals(height, that.height) &&
            Objects.equals(mimeType, that.mimeType) &&
            Objects.equals(tags, that.tags) &&
            Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, mimeType, tags, keyword);
    }

    @Override
    public String toString() {
        return "AssetSearchFilter{" +
            "width=" + width +
            ", height=" + height +
            ", mimeType='" + mimeType + '\'' +
            ", tags=" + tags +
            ", keyword='" + keyword + '\'' +
            '}';
    }

    public static class Builder {
        private Integer width;
        private Integer height;
        private String mimeType;
        private Set<String> tags;
        private String keyword;

        public Builder withWidth(Integer width) {
            this.width = width;
            return this;
        }

        public Builder withHeight(Integer height) {
            this.height = height;
            return this;
        }

        public Builder withMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder withTags(Collection<String> tags) {
            if (tags != null) {
                this.tags = new HashSet<>(tags);
            }

            return this;
        }

        public Builder withKeyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public AssetSearchFilter build() {
            AssetSearchFilter filter = new AssetSearchFilter();

            if (width != null) {
                filter.setWidth(width);
            }
            if (height != null) {
                filter.setHeight(height);
            }
            if (mimeType != null) {
                filter.setMimeType(mimeType);
            }
            if (tags != null) {
                filter.setTags(tags);
            }
            if (keyword != null) {
                filter.setKeyword(keyword);
            }

            return filter;
        }
    }
}

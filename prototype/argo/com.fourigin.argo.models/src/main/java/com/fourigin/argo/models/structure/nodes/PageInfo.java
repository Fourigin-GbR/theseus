package com.fourigin.argo.models.structure.nodes;

import com.fourigin.argo.models.template.TemplateReference;

import java.util.Objects;

public class PageInfo implements SiteNodeInfo {
    private String path;
    private String name;
    private String localizedName;
    private String displayName;
    private String description;
    private SiteNodeContainerInfo parent;

    private TemplateReference templateReference;

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
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
    public String getLocalizedName() {
        return localizedName;
    }

    @Override
    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public SiteNodeContainerInfo getParent() {
        return parent;
    }

    @Override
    public void setParent(SiteNodeContainerInfo parent) {
        this.parent = parent;
    }

    public String getReference() {
        StringBuilder builder = new StringBuilder(path);
        if (!path.endsWith("/")) {
            builder.append('/');
        }
        builder.append(name);
        return builder.toString();
    }

    public ContentPageReference getContentPageReference() {
        return new ContentPageReference(getPath(), getName());
    }

    public TemplateReference getTemplateReference() {
        return templateReference;
    }

    public void setTemplateReference(TemplateReference templateReference) {
        this.templateReference = templateReference;
    }

    public class ContentPageReference {
        private String parentPath;
        private String contentId;

        public ContentPageReference(String parentPath, String contentId) {
            this.parentPath = parentPath;
            this.contentId = contentId;
        }

        public String getParentPath() {
            return parentPath;
        }

        public String getContentId() {
            return contentId;
        }

        @Override
        public String toString() {
            return "ContentPageReference{" +
                "parentPath='" + parentPath + '\'' +
                ", contentId='" + contentId + '\'' +
                '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageInfo)) return false;
        PageInfo pageInfo = (PageInfo) o;
        return Objects.equals(path, pageInfo.path) &&
            Objects.equals(name, pageInfo.name) &&
            Objects.equals(localizedName, pageInfo.localizedName) &&
            Objects.equals(displayName, pageInfo.displayName) &&
            Objects.equals(description, pageInfo.description) &&
            Objects.equals(templateReference, pageInfo.templateReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name, localizedName, displayName, description, templateReference);
    }

    @Override
    public String toString() {
        return "PageInfo{" +
            "path='" + path + '\'' +
            ", name='" + name + '\'' +
            ", localizedName='" + localizedName + '\'' +
            ", displayName='" + displayName + '\'' +
            ", description='" + description + '\'' +
            ", templateReference=" + templateReference +
            '}';
    }

    @Override
    public String toTreeString(int depth) {
        StringBuilder builder = new StringBuilder();

        for(int i=0; i<depth; i++){
            builder.append('\t');
        }

        builder.append("PageInfo{");
        if(path == null) {
            builder.append(" path=<null>");
        }
        else {
            builder.append(" path='").append(path).append('\'');
        }

        if(name == null) {
            builder.append(", name=<null>");
        }
        else {
            builder.append(", name='").append(name).append('\'');
        }

        if(localizedName == null){
            builder.append(", localizedName=<null>");
        }
        else {
            builder.append(", localizedName='").append(localizedName).append('\'');
        }

        if(displayName == null){
            builder.append(", displayName=<null>");
        }
        else {
            builder.append(", displayName='").append(displayName).append('\'');
        }

        if(description == null){
            builder.append(", description=<null>");
        }
        else {
            builder.append(", description='").append(description).append('\'');
        }

        if(templateReference == null){
            builder.append(", templateReference=<null>");
        }
        else {
            builder.append(", templateReference={")
                .append(templateReference.getTemplateId()).append("/")
                .append(templateReference.getVariationId()).append(", rev: ")
                .append(templateReference.getRevision()).append('}');
        }

        builder.append('}');

        return builder.toString();
    }

    public static class Builder {
        private String path;
        private String name;
        private String localizedName;
        private String displayName;
        private String description;
        private SiteNodeContainerInfo parent;

        private TemplateReference templateReference;

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withLocalizedName(String localizedName) {
            this.localizedName = localizedName;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withParent(SiteNodeContainerInfo parent) {
            this.parent = parent;
            return this;
        }

        public Builder withTemplateReference(TemplateReference templateReference) {
            this.templateReference = templateReference;
            return this;
        }

        public PageInfo build() {
            Objects.requireNonNull(name, "name must not be null!");
            Objects.requireNonNull(templateReference, "templateReference must not be null!");

            PageInfo instance = new PageInfo();
            instance.setName(name);
            if (path != null) {
                instance.setPath(path);
            }
            instance.setLocalizedName(localizedName);
            instance.setDisplayName(displayName);
            instance.setDescription(description);
            instance.setParent(parent);
            instance.setTemplateReference(templateReference);

            return instance;
        }
    }
}

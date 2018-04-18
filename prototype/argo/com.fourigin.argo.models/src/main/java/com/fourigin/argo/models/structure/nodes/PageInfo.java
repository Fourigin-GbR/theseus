package com.fourigin.argo.models.structure.nodes;

import com.fourigin.argo.models.ChecksumGenerator;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.ContentPageMetaData;
import com.fourigin.argo.models.content.DataSourceContent;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.structure.CompileState;
import com.fourigin.argo.models.structure.ContentPageChecksum;
import com.fourigin.argo.models.template.TemplateReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;

public class PageInfo implements SiteNodeInfo {
    private String path;
    private String name;
    private String localizedName;
    private String displayName;
    private String description;
    private SiteNodeContainerInfo parent;

    private TemplateReference templateReference;
    private boolean staged;
    private CompileState compileState;
    private ContentPageChecksum checksum;

    private final Logger logger = LoggerFactory.getLogger(PageInfo.class);

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

    public void setChecksum(ContentPageChecksum checksum) {
        this.checksum = checksum;
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

    public CompileState getCompileState() {
        return compileState;
    }

    public void setCompileState(CompileState compileState) {
        this.compileState = compileState;
    }

    public boolean isStaged() {
        return staged;
    }

    public void setStaged(boolean staged) {
        this.staged = staged;
    }

    public ContentPageChecksum getChecksum() {
        return checksum;
    }

    public void buildChecksum(ContentPage contentPage) {
        ContentPageMetaData metaData = contentPage.getMetaData();
        List<ContentElement> content = contentPage.getContent();
        Collection<DataSourceContent> dataSources = contentPage.getDataSourceContents();

        String metaDataValue = ChecksumGenerator.getChecksum(metaData);
        String contentValue = ChecksumGenerator.getChecksum(content);

        Map<String, String> dataSourceValues = new HashMap<>();
        if (dataSources != null) {
            for (DataSourceContent dataSource : dataSources) {
                String name = dataSource.getName();
                String dataSourceChecksum = ChecksumGenerator.getChecksum(dataSource.getContent());
                if (logger.isDebugEnabled())
                    logger.debug("Put '{}': '{}' data source checksum value", name, dataSourceChecksum);
                dataSourceValues.put(name, dataSourceChecksum);
            }
        }

        this.checksum = new ContentPageChecksum(metaDataValue, contentValue, dataSourceValues);
    }

    public void setChecksum(String metaDataValue, String contentValue, SortedMap<String, String> dataSourceValues) {
        this.checksum = new ContentPageChecksum(metaDataValue, contentValue, dataSourceValues);
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

        if (staged != pageInfo.staged) return false;
        if (path != null ? !path.equals(pageInfo.path) : pageInfo.path != null) return false;
        if (name != null ? !name.equals(pageInfo.name) : pageInfo.name != null) return false;
        if (localizedName != null ? !localizedName.equals(pageInfo.localizedName) : pageInfo.localizedName != null)
            return false;
        if (displayName != null ? !displayName.equals(pageInfo.displayName) : pageInfo.displayName != null)
            return false;
        if (description != null ? !description.equals(pageInfo.description) : pageInfo.description != null)
            return false;
        if (templateReference != null ? !templateReference.equals(pageInfo.templateReference) : pageInfo.templateReference != null)
            return false;
        //noinspection SimplifiableIfStatement
        if (compileState != null ? !compileState.equals(pageInfo.compileState) : pageInfo.compileState != null)
            return false;
        return checksum != null ? checksum.equals(pageInfo.checksum) : pageInfo.checksum == null;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (localizedName != null ? localizedName.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (templateReference != null ? templateReference.hashCode() : 0);
        result = 31 * result + (staged ? 1 : 0);
        result = 31 * result + (compileState != null ? compileState.hashCode() : 0);
        result = 31 * result + (checksum != null ? checksum.hashCode() : 0);
        return result;
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
            ", staged=" + staged +
            ", compileState=" + compileState +
            ", checksum=" + checksum +
            '}';
    }

    public static class Builder {
        private String path;
        private String name;
        private String localizedName;
        private String displayName;
        private String description;
        private SiteNodeContainerInfo parent;

        private TemplateReference templateReference;
        private boolean staged;
        private CompileState compileState;
        private ContentPageChecksum checksum;

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

        public Builder withStaged(boolean staged) {
            this.staged = staged;
            return this;
        }

        public Builder withCompileState(CompileState compileState) {
            this.compileState = compileState;
            return this;
        }

        public Builder withChecksum(ContentPageChecksum checksum) {
            this.checksum = checksum;
            return this;
        }

        public PageInfo build() {
            Objects.requireNonNull(name, "name must not be null!");
            Objects.requireNonNull(templateReference, "templateReference must not be null!");
            Objects.requireNonNull(compileState, "compileState must not be null!");

            PageInfo instance = new PageInfo();
            instance.setName(name);
            if (path != null) {
                instance.setPath(path);
            }
            if (checksum != null) {
                instance.setChecksum(checksum);
            }
            instance.setLocalizedName(localizedName);
            instance.setDisplayName(displayName);
            instance.setDescription(description);
            instance.setParent(parent);
            instance.setTemplateReference(templateReference);
            instance.setStaged(staged);
            instance.setCompileState(compileState);

            return instance;
        }
    }
}

package com.fourigin.argo.models.structure.nodes;

import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import com.fourigin.argo.models.structure.CompileState;
import com.fourigin.argo.models.template.TemplateReference;

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
    private Checksum checksum;

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

    public void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }

    public String getReference(){
        StringBuilder builder = new StringBuilder(path);
        if(!path.endsWith("/")){
            builder.append('/');
        }
        builder.append(name);
        return builder.toString();
    }

    public ContentPageReference getContentPageReference(){
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

    public Checksum getChecksum() {
        return checksum;
    }

    public void setChecksum(String contentChecksum, SortedMap<DataSourceIdentifier, String> dataSourceChecksum) {
        this.checksum = new Checksum(contentChecksum, dataSourceChecksum);
    }

    public class Checksum {
        private String contentChecksum;
        private SortedMap<DataSourceIdentifier, String> dataSourceChecksum;

        public Checksum(String contentChecksum, SortedMap<DataSourceIdentifier, String> dataSourceChecksum) {
            this.contentChecksum = contentChecksum;
            this.dataSourceChecksum = dataSourceChecksum;
        }

        public String getCombinedChecksum(){
            StringBuilder result = new StringBuilder(contentChecksum);
            for (String checksum : dataSourceChecksum.values()) {
                result.append('-');
                result.append(checksum);
            }
            return result.toString();
        }
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

}


package com.fourigin.argo.repository.model;

import com.fourigin.argo.models.structure.CompileState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.template.TemplateReference;

public class JsonFileInfo implements JsonInfo<PageInfo> {
    private String path;
    private String name;
    private String localizedName;
    private String displayName;
    private String description;

    private TemplateReference templateReference;
    private boolean staged;
    private CompileState compileState;
    private PageInfo.Checksum checksum;

    public JsonFileInfo() {
    }

    public JsonFileInfo(PageInfo nodeInfo){
        if(nodeInfo != null) {
            this.path = nodeInfo.getPath();
            this.name = nodeInfo.getName();
            this.localizedName = nodeInfo.getLocalizedName();
            this.displayName = nodeInfo.getDisplayName();
            this.description = nodeInfo.getDescription();

            this.templateReference = nodeInfo.getTemplateReference();
            this.staged = nodeInfo.isStaged();
            this.compileState = nodeInfo.getCompileState();
            this.checksum = nodeInfo.getChecksum();
        }
    }

    @Override
    public PageInfo buildNodeInfo() {
        PageInfo info = new PageInfo();

        info.setPath(path);
        info.setName(name);
        info.setName(localizedName);
        info.setName(displayName);
        info.setDescription(description);

        info.setTemplateReference(templateReference);
        info.setStaged(staged);
        info.setCompileState(compileState);
        info.setChecksum(checksum);

        return info;
    }

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

    public TemplateReference getTemplateReference() {
        return templateReference;
    }

    public void setTemplateReference(TemplateReference templateReference) {
        this.templateReference = templateReference;
    }

    public boolean isStaged() {
        return staged;
    }

    public void setStaged(boolean staged) {
        this.staged = staged;
    }

    public CompileState getCompileState() {
        return compileState;
    }

    public void setCompileState(CompileState compileState) {
        this.compileState = compileState;
    }

    public PageInfo.Checksum getChecksum() {
        return checksum;
    }

    public void setChecksum(PageInfo.Checksum checksum) {
        this.checksum = checksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonFileInfo)) return false;

        JsonFileInfo that = (JsonFileInfo) o;

        if (staged != that.staged) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (localizedName != null ? !localizedName.equals(that.localizedName) : that.localizedName != null)
            return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (templateReference != null ? !templateReference.equals(that.templateReference) : that.templateReference != null)
            return false;
        //noinspection SimplifiableIfStatement
        if (compileState != null ? !compileState.equals(that.compileState) : that.compileState != null) return false;
        return checksum != null ? checksum.equals(that.checksum) : that.checksum == null;
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
        return "JsonFileInfo{" +
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

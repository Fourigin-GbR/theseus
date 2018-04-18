package com.fourigin.argo.repository.model;

import com.fourigin.argo.models.structure.CompileState;
import com.fourigin.argo.models.structure.ContentPageChecksum;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.models.template.TemplateReference;

import java.util.Objects;

public class JsonFileInfo implements JsonInfo<PageInfo> {
    private String name;
    private String localizedName;
    private String displayName;
    private String description;

    private TemplateReference templateReference;
    private boolean staged;
    private CompileState compileState;
    private ContentPageChecksum checksum;

    public JsonFileInfo() {
    }

    public JsonFileInfo(PageInfo nodeInfo){
        if(nodeInfo != null) {
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
        return new PageInfo.Builder()
            .withName(name)
            .withLocalizedName(localizedName)
            .withDisplayName(displayName)
            .withDescription(description)
            .withTemplateReference(templateReference)
            .withStaged(staged)
            .withCompileState(compileState)
            .withChecksum(checksum)
            .build();
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

    public ContentPageChecksum getChecksum() {
        return checksum;
    }

    public void setChecksum(ContentPageChecksum checksum) {
        this.checksum = checksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonFileInfo)) return false;

        JsonFileInfo that = (JsonFileInfo) o;

        if (staged != that.staged) return false;
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
        return Objects.hash(name, localizedName, displayName, description, templateReference, staged, compileState, checksum);
    }

    @Override
    public String toString() {
        return "JsonFileInfo{" +
            "name='" + name + '\'' +
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

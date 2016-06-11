package com.fourigin.cms.models.content;

import com.fourigin.cms.models.content.elements.ContentElement;

import java.util.Collection;
import java.util.List;

public class ContentPage {
    private String id;
    private String revision;
    private boolean staged;
    private CompileState compileState;
    private ContentPageMetaData metaData;
    private List<ContentElement> content;
    private Collection<DataSourceContent> dataSourceContents;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
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

    public ContentPageMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(ContentPageMetaData metaData) {
        this.metaData = metaData;
    }

    public List<ContentElement> getContent() {
        return content;
    }

    public void setContent(List<ContentElement> content) {
        this.content = content;
    }

    public Collection<DataSourceContent> getDataSourceContents() {
        return dataSourceContents;
    }

    public void setDataSourceContents(Collection<DataSourceContent> dataSourceContents) {
        this.dataSourceContents = dataSourceContents;
    }
}

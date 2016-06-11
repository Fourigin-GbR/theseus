package com.fourigin.cms.models;

import com.fourigin.cms.models.elements.ContentElement;

import java.util.Collection;
import java.util.List;

public class ContentPage {
    private String name;
    private String revision;
    private boolean staged;
    private CompileState compileState;
    private ContentPageMetaData metaData;
    private List<ContentElement> content;
    private Collection<ContentDataSource> dataSources;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Collection<ContentDataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(Collection<ContentDataSource> dataSources) {
        this.dataSources = dataSources;
    }
}

package com.fourigin.cms.models.content;

import com.fourigin.cms.models.content.elements.ContentElement;

import java.util.Collection;
import java.util.List;

public class ContentPage {
    private String id;
    private String revision;
//    private boolean staged;
//    private CompileState compileState;
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

    @Override
    public String toString() {
        return "ContentPage{" +
          "id='" + id + '\'' +
          ", revision='" + revision + '\'' +
          ", metaData=" + metaData +
          ", content=" + content +
          ", dataSourceContents=" + dataSourceContents +
          '}';
    }

    public static class Builder {
        private String id;
        private String revision;
        private ContentPageMetaData metaData;
        private List<ContentElement> content;
        private Collection<DataSourceContent> dataSourceContents;

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder revision(String revision){
            this.revision = revision;
            return this;
        }

        public Builder metaData(ContentPageMetaData metaData){
            this.metaData = metaData;
            return this;
        }

        public Builder content(List<ContentElement> content){
            this.content = content;
            return this;
        }

        public Builder dataSourceContents(Collection<DataSourceContent> dataSourceContents){
            this.dataSourceContents = dataSourceContents;
            return this;
        }

        public ContentPage build(){
            ContentPage page = new ContentPage();
            page.setId(id);
            page.setRevision(revision);
            page.setMetaData(metaData);
            page.setContent(content);
            page.setDataSourceContents(dataSourceContents);
            return page;
        }
    }
}

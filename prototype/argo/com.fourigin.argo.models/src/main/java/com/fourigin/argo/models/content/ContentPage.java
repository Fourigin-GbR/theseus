package com.fourigin.argo.models.content;

import com.fourigin.argo.models.content.elements.ContentElement;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class ContentPage implements Serializable {
    private static final long serialVersionUID = -6314470617724474054L;

    private String id;
    private ContentPageMetaData metaData;
    private List<ContentElement> content;
    private Collection<DataSourceContent> dataSourceContents;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
            ", metaData=" + metaData +
            ", content=" + content +
            ", dataSourceContents=" + dataSourceContents +
            '}';
    }

    public static class Builder {
        private String id;
        private ContentPageMetaData metaData;
        private List<ContentElement> content;
        private Collection<DataSourceContent> dataSourceContents;

        public Builder withId(String id){
            this.id = id;
            return this;
        }

        public Builder withMetaData(ContentPageMetaData metaData){
            this.metaData = metaData;
            return this;
        }

        public Builder withContent(List<ContentElement> content){
            this.content = content;
            return this;
        }

        public Builder withDataSourceContents(Collection<DataSourceContent> dataSourceContents){
            this.dataSourceContents = dataSourceContents;
            return this;
        }

        public ContentPage build(){
            ContentPage page = new ContentPage();
            page.setId(id);
            page.setMetaData(metaData);
            page.setContent(content);
            page.setDataSourceContents(dataSourceContents);
            return page;
        }
    }
}

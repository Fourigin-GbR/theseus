package com.fourigin.argo.models.content;

import com.fourigin.argo.models.content.config.RuntimeConfigurations;
import com.fourigin.argo.models.content.elements.ContentElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ContentPage implements Serializable {
    private static final long serialVersionUID = -6314470617724474054L;

    private String id;
    private String contentFormatVersion;
    private ContentPageMetaData metaData;
    private List<ContentElement> content;
    private Collection<DataSourceContent> dataSourceContents;
    private RuntimeConfigurations configurations;

    public ContentPage() {
    }

    public ContentPage(ContentPage prototype) {
        this.id = prototype.id;
        this.metaData = new ContentPageMetaData(prototype.metaData);
        this.content = new ArrayList<>(prototype.content);
        this.dataSourceContents = new ArrayList<>(prototype.dataSourceContents);
    }

    public boolean hasDataSourceContents(){
        return dataSourceContents != null && !dataSourceContents.isEmpty();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentFormatVersion() {
        return contentFormatVersion;
    }

    public void setContentFormatVersion(String contentFormatVersion) {
        this.contentFormatVersion = contentFormatVersion;
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

    public RuntimeConfigurations getConfigurations() {
        return configurations;
    }

    public void setConfigurations(RuntimeConfigurations configurations) {
        this.configurations = configurations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentPage)) return false;
        ContentPage that = (ContentPage) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(metaData, that.metaData) &&
            Objects.equals(content, that.content) &&
            Objects.equals(dataSourceContents, that.dataSourceContents) &&
            Objects.equals(configurations, that.configurations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, metaData, content, dataSourceContents, configurations);
    }

    @Override
    public String toString() {
        return "ContentPage{" +
            "id='" + id + '\'' +
            ", metaData=" + metaData +
            ", content=" + content +
            ", dataSourceContents=" + dataSourceContents +
            ", configurations=" + configurations +
            '}';
    }

    public static class Builder {
        private String id;
        private ContentPageMetaData metaData;
        private List<ContentElement> content;
        private Collection<DataSourceContent> dataSourceContents;
        private RuntimeConfigurations configurations;

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

        public Builder withConfigurations(RuntimeConfigurations configurations){
            this.configurations = configurations;
            return this;
        }

        public ContentPage build(){
            ContentPage page = new ContentPage();
            page.setContentFormatVersion("2.0");
            page.setId(id);
            page.setMetaData(metaData);
            page.setContent(content);
            page.setDataSourceContents(dataSourceContents);
            page.setConfigurations(configurations);
            return page;
        }
    }
}

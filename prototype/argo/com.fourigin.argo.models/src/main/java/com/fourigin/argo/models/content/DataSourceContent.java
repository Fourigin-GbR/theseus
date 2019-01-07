package com.fourigin.argo.models.content;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class DataSourceContent implements Serializable {
    private static final long serialVersionUID = 5480644248235587413L;

    private String name;
    private DataSourceIdentifier identifier;
    private List<ContentElement> content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataSourceIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(DataSourceIdentifier identifier) {
        this.identifier = identifier;
    }

    public List<ContentElement> getContent() {
        return content;
    }

    public void setContent(List<ContentElement> content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSourceContent)) return false;
        DataSourceContent that = (DataSourceContent) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(identifier, that.identifier) &&
            Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, identifier, content);
    }

    @Override
    public String toString() {
        return "DataSourceContent{" +
            "name='" + name + '\'' +
            ", identifier=" + identifier +
            ", content=" + content +
            '}';
    }

    public static class Builder {
        private String name;
        private DataSourceIdentifier identifier;
        private List<ContentElement> content;

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withIdentifier(DataSourceIdentifier identifier){
            this.identifier = identifier;
            return this;
        }

        public Builder withContent(List<ContentElement> content){
            this.content = content;
            return this;
        }

        public DataSourceContent build(){
            DataSourceContent result = new DataSourceContent();

            Objects.requireNonNull(name, "name must not be null!");
            Objects.requireNonNull(identifier, "identifier must not be null!");

            result.setName(name);
            result.setIdentifier(identifier);

            if(content != null){
                result.setContent(content);
            }

            return result;
        }
    }
}

package com.fourigin.argo.models.content;

import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;

import java.io.Serializable;

public class DataSourceContent implements Serializable {
    private static final long serialVersionUID = 5480644248235587413L;

    private String name;
    private DataSourceIdentifier identifier;
    private ContentElement content;

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

    public ContentElement getContent() {
        return content;
    }

    public void setContent(ContentElement content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSourceContent)) return false;

        DataSourceContent that = (DataSourceContent) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        //noinspection SimplifiableIfStatement
        if (identifier != null ? !identifier.equals(that.identifier) : that.identifier != null) return false;
        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataSourceContent{" +
            "name='" + name + '\'' +
            ", identifier=" + identifier +
            ", content=" + content +
            '}';
    }
}

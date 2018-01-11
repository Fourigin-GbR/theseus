package com.fourigin.argo.models.template;

public class TemplateVariation {
    private String id;
    private String description;
    private Type type;
    private String outputContentType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getOutputContentType() {
        return outputContentType;
    }

    public void setOutputContentType(String outputContentType) {
        this.outputContentType = outputContentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemplateVariation)) return false;

        TemplateVariation that = (TemplateVariation) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        //noinspection SimplifiableIfStatement
        if (type != that.type) return false;
        return outputContentType != null ? outputContentType.equals(that.outputContentType) : that.outputContentType == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (outputContentType != null ? outputContentType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TemplateVariation{" +
            "id='" + id + '\'' +
            ", description='" + description + '\'' +
            ", type=" + type +
            ", outputContentType='" + outputContentType + '\'' +
            '}';
    }
}

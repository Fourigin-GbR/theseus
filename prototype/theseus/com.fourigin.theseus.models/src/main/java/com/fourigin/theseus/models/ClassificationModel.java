package com.fourigin.theseus.models;

public class ClassificationModel implements ModelObject {
    private String id;
    private String revision;
    private String typeCode;
    private String description;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getRevision() {
        return revision;
    }

    @Override
    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassificationModel)) return false;

        ClassificationModel that = (ClassificationModel) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (revision != null ? !revision.equals(that.revision) : that.revision != null) return false;
        //noinspection SimplifiableIfStatement
        if (typeCode != null ? !typeCode.equals(that.typeCode) : that.typeCode != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
        result = 31 * result + (typeCode != null ? typeCode.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClassificationModel{" +
          "id='" + id + '\'' +
          ", revision='" + revision + '\'' +
          ", typeCode='" + typeCode + '\'' +
          ", description='" + description + '\'' +
          '}';
    }

    public static class Builder {
        private String id;
        private String revision;
        private String typeCode;
        private String description;

        public Builder id(String code){
            this.id = code;
            return this;
        }

        public Builder revision(String revision){
            this.revision = revision;
            return this;
        }

        public Builder typeCode(String typeCode){
            this.typeCode = typeCode;
            return this;
        }

        public Builder description(String description){
            this.description = description;
            return this;
        }

        public ClassificationModel build(){
            ClassificationModel model = new ClassificationModel();
            model.setId(id);
            model.setRevision(revision);
            model.setTypeCode(typeCode);
            model.setDescription(description);

            reset();

            return model;
        }

        public void reset(){
            this.id = null;
            this.revision = null;
            this.typeCode = null;
            this.description = null;
        }
    }
}
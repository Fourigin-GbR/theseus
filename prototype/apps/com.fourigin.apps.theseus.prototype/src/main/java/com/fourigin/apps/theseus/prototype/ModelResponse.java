package com.fourigin.apps.theseus.prototype;

public class ModelResponse {
    private String code;
    private String revision;
    private int length;
    private String description;

    public ModelResponse(String code, String revision, int length, String description) {
        this.code = code;
        this.revision = revision;
        this.length = length;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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
        if (!(o instanceof ModelResponse)) return false;

        ModelResponse testModel = (ModelResponse) o;

        if (length != testModel.length) return false;
        if (code != null ? !code.equals(testModel.code) : testModel.code != null) return false;
        //noinspection SimplifiableIfStatement
        if (revision != null ? !revision.equals(testModel.revision) : testModel.revision != null) return false;
        return description != null ? description.equals(testModel.description) : testModel.description == null;

    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
        result = 31 * result + length;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestModel{" +
          "code='" + code + '\'' +
          ", revision='" + revision + '\'' +
          ", length=" + length +
          ", description='" + description + '\'' +
          '}';
    }
}
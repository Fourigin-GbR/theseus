package com.fourigin.theseus.core.types;

public class EditablePropertyType implements PropertyType {
    private String name;
    private String description;
    private String pattern;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EditablePropertyType)) return false;

        EditablePropertyType that = (EditablePropertyType) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        //noinspection SimplifiableIfStatement
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return pattern != null ? pattern.equals(that.pattern) : that.pattern == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (pattern != null ? pattern.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SimplePropertyType{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", pattern='" + pattern + '\'' +
            '}';
    }
}

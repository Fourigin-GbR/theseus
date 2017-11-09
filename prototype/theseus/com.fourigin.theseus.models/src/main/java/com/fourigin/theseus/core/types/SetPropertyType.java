package com.fourigin.theseus.core.types;

import java.util.Set;

public class SetPropertyType implements PropertyType {
    private String name;
    private String description;
    private Set<String> keys;

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

    public Set<String> getKeys() {
        return keys;
    }

    public void setKeys(Set<String> keys) {
        this.keys = keys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SetPropertyType)) return false;

        SetPropertyType that = (SetPropertyType) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        //noinspection SimplifiableIfStatement
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return keys != null ? keys.equals(that.keys) : that.keys == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (keys != null ? keys.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PredefinedValuesPropertyType{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", keys=" + keys +
            '}';
    }
}

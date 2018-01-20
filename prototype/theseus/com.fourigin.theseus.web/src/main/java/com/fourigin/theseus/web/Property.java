package com.fourigin.theseus.web;

import com.fourigin.theseus.core.Translation;

public class Property {
    private String code;
    private String type;
    private Translation name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Translation getName() {
        return name;
    }

    public void setName(Translation name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property)) return false;

        Property property = (Property) o;

        if (code != null ? !code.equals(property.code) : property.code != null) return false;
        //noinspection SimplifiableIfStatement
        if (type != null ? !type.equals(property.type) : property.type != null) return false;
        return name != null ? name.equals(property.name) : property.name == null;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}

package com.fourigin.theseus;

import com.fourigin.theseus.core.Translation;
import com.fourigin.theseus.core.types.PropertyType;

import java.util.List;
import java.util.Set;

public class PropertyDefinitionsResponse {
    private List<Property> properties;
    private Set<PropertyType> types;

    public PropertyDefinitionsResponse(List<Property> properties, Set<PropertyType> types) {
        this.properties = properties;
        this.types = types;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public Set<PropertyType> getTypes() {
        return types;
    }

    public void setTypes(Set<PropertyType> types) {
        this.types = types;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyDefinitionsResponse)) return false;

        PropertyDefinitionsResponse that = (PropertyDefinitionsResponse) o;

        //noinspection SimplifiableIfStatement
        if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;
        return types != null ? types.equals(that.types) : that.types == null;
    }

    @Override
    public int hashCode() {
        int result = properties != null ? properties.hashCode() : 0;
        result = 31 * result + (types != null ? types.hashCode() : 0);
        return result;
    }
}

class Property {
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

package com.fourigin.argo.forms.definition;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class FormObjectMapperDefinition implements Serializable {
    private static final long serialVersionUID = 4722377491210268682L;

    private String type;
    private Map<String, Object> settings;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormObjectMapperDefinition)) return false;
        FormObjectMapperDefinition that = (FormObjectMapperDefinition) o;
        return Objects.equals(type, that.type) &&
            Objects.equals(settings, that.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, settings);
    }

    @Override
    public String toString() {
        return "FormObjectMapperDefinition{" +
            "type='" + type + '\'' +
            ", settings=" + settings +
            '}';
    }
}

package com.fourigin.argo.models.content.config;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class RuntimeConfiguration implements Serializable {
    private static final long serialVersionUID = 4340365682998042684L;

    private String name;
    private Map<String, Object> settings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(o instanceof RuntimeConfiguration)) return false;
        RuntimeConfiguration that = (RuntimeConfiguration) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(settings, that.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, settings);
    }

    @Override
    public String toString() {
        return "RuntimeConfiguration{" +
            "name='" + name + '\'' +
            ", settings=" + settings +
            '}';
    }
}

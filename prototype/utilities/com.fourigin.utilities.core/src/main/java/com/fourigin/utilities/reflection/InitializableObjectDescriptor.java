package com.fourigin.utilities.reflection;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class InitializableObjectDescriptor implements Serializable {
    private static final long serialVersionUID = 6988987935652375907L;

    private String targetClass;
    private Map<String, Object> settings;

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
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
        if (!(o instanceof InitializableObjectDescriptor)) return false;
        InitializableObjectDescriptor that = (InitializableObjectDescriptor) o;
        return Objects.equals(targetClass, that.targetClass) &&
            Objects.equals(settings, that.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetClass, settings);
    }

    @Override
    public String toString() {
        return "InitializableObjectDescriptor{" +
            "targetClass='" + targetClass + '\'' +
            ", settings=" + settings +
            '}';
    }
}

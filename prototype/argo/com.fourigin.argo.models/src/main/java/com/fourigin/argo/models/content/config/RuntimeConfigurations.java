package com.fourigin.argo.models.content.config;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class RuntimeConfigurations implements Serializable {
    private static final long serialVersionUID = 3274521420793118155L;
    
    private Set<String> names;
    private Set<RuntimeConfiguration> configurations;

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }

    public Set<RuntimeConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Set<RuntimeConfiguration> configurations) {
        this.configurations = configurations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuntimeConfigurations)) return false;
        RuntimeConfigurations that = (RuntimeConfigurations) o;
        return Objects.equals(names, that.names) &&
            Objects.equals(configurations, that.configurations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(names, configurations);
    }

    @Override
    public String toString() {
        return "RuntimeConfigurations{" +
            "names=" + names +
            ", configurations=" + configurations +
            '}';
    }
}

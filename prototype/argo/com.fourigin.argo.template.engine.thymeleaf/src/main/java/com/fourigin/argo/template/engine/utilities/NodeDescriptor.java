package com.fourigin.argo.template.engine.utilities;

import java.io.Serializable;
import java.util.Objects;

public class NodeDescriptor implements Serializable {
    private static final long serialVersionUID = 8748838931220297339L;

    private String path;
    private String name;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeDescriptor)) return false;
        NodeDescriptor that = (NodeDescriptor) o;
        return Objects.equals(path, that.path) &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name);
    }

    @Override
    public String toString() {
        return "NodeDescriptor{" +
            "path='" + path + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}

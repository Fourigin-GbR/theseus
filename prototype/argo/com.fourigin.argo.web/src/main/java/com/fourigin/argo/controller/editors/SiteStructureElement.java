package com.fourigin.argo.controller.editors;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class SiteStructureElement implements Serializable {
    private static final long serialVersionUID = -8052377225625155936L;

    private SiteStructureElementType type;
    private String name;
    private List<SiteStructureElement> children;

    public SiteStructureElementType getType() {
        return type;
    }

    public void setType(SiteStructureElementType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SiteStructureElement> getChildren() {
        return children;
    }

    public void setChildren(List<SiteStructureElement> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteStructureElement that = (SiteStructureElement) o;
        return type == that.type &&
                Objects.equals(name, that.name) &&
                Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, children);
    }

    @Override
    public String toString() {
        return "SiteStructureElement{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", children=" + children +
                '}';
    }
}

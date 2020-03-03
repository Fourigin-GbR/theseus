package com.fourigin.argo.models.action;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SiteNodes implements Serializable {
    private static final long serialVersionUID = 8076289380864080682L;

    private Set<SiteNode> nodes;
    private List<SiteStructureElement> structure;

    public Set<SiteNode> getNodes() {
        return nodes;
    }

    public void setNodes(Set<SiteNode> nodes) {
        this.nodes = nodes;
    }

    public List<SiteStructureElement> getStructure() {
        return structure;
    }

    public void setStructure(List<SiteStructureElement> structure) {
        this.structure = structure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteNodes siteNodes = (SiteNodes) o;
        return Objects.equals(nodes, siteNodes.nodes) &&
                Objects.equals(structure, siteNodes.structure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, structure);
    }

    @Override
    public String toString() {
        return "SiteNodes{" +
                "nodes=" + nodes +
                ", structure=" + structure +
                '}';
    }
}

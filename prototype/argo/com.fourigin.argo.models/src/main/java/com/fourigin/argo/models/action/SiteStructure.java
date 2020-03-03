package com.fourigin.argo.models.action;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class SiteStructure implements Serializable {
    private static final long serialVersionUID = -20690181274046082L;

    private List<SiteStructureElement> structure;

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
        SiteStructure that = (SiteStructure) o;
        return Objects.equals(structure, that.structure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(structure);
    }

    @Override
    public String toString() {
        return "SiteStructure{" +
                "structure=" + structure +
                '}';
    }
}

package com.fourigin.argo.controller.editors;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class SiteStructure implements Serializable {
    private static final long serialVersionUID = -20690181274046082L;

    private String originalChecksum;
    private List<SiteStructureElement> structure;

    public String getOriginalChecksum() {
        return originalChecksum;
    }

    public void setOriginalChecksum(String originalChecksum) {
        this.originalChecksum = originalChecksum;
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
        SiteStructure that = (SiteStructure) o;
        return Objects.equals(originalChecksum, that.originalChecksum) &&
                Objects.equals(structure, that.structure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalChecksum, structure);
    }

    @Override
    public String toString() {
        return "SiteStructureResponse{" +
                "currentChecksum='" + originalChecksum + '\'' +
                ", structure=" + structure +
                '}';
    }
}

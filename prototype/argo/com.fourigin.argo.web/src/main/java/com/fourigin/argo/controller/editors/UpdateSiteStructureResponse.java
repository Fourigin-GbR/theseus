package com.fourigin.argo.controller.editors;

import java.io.Serializable;
import java.util.Objects;

public class UpdateSiteStructureResponse implements Serializable {
    private static final long serialVersionUID = -3789563327705231357L;

    private boolean status;
    private SiteStructure siteStructure;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public SiteStructure getSiteStructure() {
        return siteStructure;
    }

    public void setSiteStructure(SiteStructure siteStructure) {
        this.siteStructure = siteStructure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateSiteStructureResponse that = (UpdateSiteStructureResponse) o;
        return status == that.status &&
                Objects.equals(siteStructure, that.siteStructure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, siteStructure);
    }

    @Override
    public String toString() {
        return "UpdateSiteStructureResponse{" +
                "status=" + status +
                ", siteStructure=" + siteStructure +
                '}';
    }
}

package com.fourigin.argo.controller.editors;

import java.io.Serializable;
import java.util.Objects;

public class UpdateSiteNodeResponse implements Serializable {
    private static final long serialVersionUID = -2472968636110056363L;

    private boolean status;
    private SiteNode siteNode;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public SiteNode getSiteNode() {
        return siteNode;
    }

    public void setSiteNode(SiteNode siteNode) {
        this.siteNode = siteNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateSiteNodeResponse that = (UpdateSiteNodeResponse) o;
        return status == that.status &&
                Objects.equals(siteNode, that.siteNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, siteNode);
    }

    @Override
    public String toString() {
        return "UpdateSiteNodeResponse{" +
                "status=" + status +
                ", siteNode=" + siteNode +
                '}';
    }
}

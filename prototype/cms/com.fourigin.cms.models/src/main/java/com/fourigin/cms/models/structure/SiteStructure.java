package com.fourigin.cms.models.structure;

import com.fourigin.cms.models.structure.nodes.SiteNode;

import java.util.List;

public class SiteStructure {
    private String id;

    private String revision;

    private List<SiteNode> nodes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public List<SiteNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<SiteNode> nodes) {
        this.nodes = nodes;
    }
}

package com.fourigin.argo.repository.model;

import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

import java.util.Map;

public interface JsonInfo<T extends SiteNodeInfo> {
    String getName();
    void setName(String name);

    Map<String, String> getLocalizedName();
    void setLocalizedName(Map<String, String> name);

    Map<String, String> getDisplayName();
    void setDisplayName(Map<String, String> name);

    String getDescription();
    void setDescription(String description);

    T buildNodeInfo();
}

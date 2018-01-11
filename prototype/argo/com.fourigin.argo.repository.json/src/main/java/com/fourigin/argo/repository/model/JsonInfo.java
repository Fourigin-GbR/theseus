package com.fourigin.argo.repository.model;

import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

public interface JsonInfo<T extends SiteNodeInfo> {
    String getPath();
    void setPath(String path);

    String getName();
    void setName(String name);

    String getLocalizedName();
    void setLocalizedName(String name);

    String getDisplayName();
    void setDisplayName(String name);

    String getDescription();
    void setDescription(String description);

    T buildNodeInfo();
}

package com.fourigin.argo.models.structure.nodes;

import java.io.Serializable;
import java.util.Map;

public interface SiteNodeInfo extends Serializable {
    String getPath();
    void setPath(String path);

    String getName();
    void setName(String name);

    Map<String, String> getLocalizedName();
    void setLocalizedName(Map<String, String> name);

    Map<String, String> getDisplayName();
    void setDisplayName(Map<String, String> name);

    String getDescription();
    void setDescription(String description);

    SiteNodeContainerInfo getParent();
    void setParent(SiteNodeContainerInfo siteNode);

    String toTreeString(int depth);

    default String getReference(){
        String path = getPath();
        String name = getName();

        StringBuilder builder = new StringBuilder(path);
        if (!path.endsWith("/")) {
            builder.append('/');
        }
        builder.append(name);

        return builder.toString();
    }
}

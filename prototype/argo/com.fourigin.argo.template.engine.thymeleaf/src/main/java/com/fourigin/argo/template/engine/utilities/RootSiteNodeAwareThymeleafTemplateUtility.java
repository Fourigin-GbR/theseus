package com.fourigin.argo.template.engine.utilities;

import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;

public interface RootSiteNodeAwareThymeleafTemplateUtility {
    void setRootSiteNode(SiteNodeContainerInfo root);

}

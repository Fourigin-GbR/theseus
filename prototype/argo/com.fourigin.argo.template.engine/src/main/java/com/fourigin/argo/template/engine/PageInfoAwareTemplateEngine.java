package com.fourigin.argo.template.engine;

import com.fourigin.argo.models.structure.nodes.PageInfo;

public interface PageInfoAwareTemplateEngine {
    void setPageInfo(PageInfo pageInfo);
}

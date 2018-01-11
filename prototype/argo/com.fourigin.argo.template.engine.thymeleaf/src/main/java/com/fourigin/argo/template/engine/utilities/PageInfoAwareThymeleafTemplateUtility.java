package com.fourigin.argo.template.engine.utilities;

import com.fourigin.argo.models.structure.nodes.PageInfo;

public interface PageInfoAwareThymeleafTemplateUtility {
    void setPageInfo(PageInfo pageInfo);
}

package com.fourigin.cms.template.engine.utilities;

import com.fourigin.cms.models.structure.nodes.PageInfo;

public interface PageInfoAwareThymeleafTemplateUtility {
    void setPageInfo(PageInfo pageInfo);
}

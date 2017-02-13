package com.fourigin.cms.template.engine;

import com.fourigin.cms.models.structure.nodes.PageInfo;

public interface PageInfoAwareTemplateEngine {
    void setPageInfo(PageInfo pageInfo);
}

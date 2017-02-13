package com.fourigin.cms.template.engine.utilities;

import com.fourigin.cms.models.content.ContentPage;

public interface ContentPageAwareThymeleafTemplateUtility extends ThymeleafTemplateUtility {
    void setContentPage(ContentPage contentPage);
}

package com.fourigin.argo.template.engine.utilities;

import com.fourigin.argo.models.content.ContentPage;

public interface ContentPageAwareThymeleafTemplateUtility extends ThymeleafTemplateUtility {
    void setContentPage(ContentPage contentPage);
}

package com.fourigin.argo.compiler;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.template.engine.ProcessingMode;

public interface CompilerInterceptor {
    void afterPrepareContent(String base, String path, PageInfo pageInfo, ProcessingMode processingMode, ContentPage contentPage);
}

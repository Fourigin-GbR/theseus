package com.fourigin.argo.compiler.processor;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.template.engine.ProcessingMode;

public interface ContentPageProcessor {
    void process(String customer, String locale, PageInfo info, ProcessingMode processingMode, ContentPage page);
}

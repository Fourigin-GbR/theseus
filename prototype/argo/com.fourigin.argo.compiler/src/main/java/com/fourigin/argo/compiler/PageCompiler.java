package com.fourigin.argo.compiler;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.template.engine.ProcessingMode;

import java.io.OutputStream;

public interface PageCompiler {

    ContentPage prepareContent(PageInfo pageInfo);

    /**
     *
     * @param pageInfo PageInfo with all information about the page to compile.
     * @param processingMode the mode to compile
     * @param out OutputStream where the result should be written.
     * @return Returns the content type of the compiled result.
     */
    String compile(PageInfo pageInfo, ProcessingMode processingMode, OutputStream out);
}

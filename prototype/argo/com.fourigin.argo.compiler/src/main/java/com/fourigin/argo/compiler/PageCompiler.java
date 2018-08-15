package com.fourigin.argo.compiler;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.strategies.CompilerOutputStrategy;
import com.fourigin.argo.template.engine.ProcessingMode;

public interface PageCompiler {
    String getCompilerBase();

    ContentPage prepareContent(PageInfo pageInfo);

    /**
     *
     * @param path Tha site structure path of the page to compile.
     * @param pageInfo PageInfo with all information about the page to compile.
     * @param processingMode the mode to compile
     * @param outputStrategy {@link CompilerOutputStrategy} where the result should be written.
     * @return Returns the content type of the compiled result.
     */
    String compile(String path, PageInfo pageInfo, ProcessingMode processingMode, CompilerOutputStrategy outputStrategy);
}

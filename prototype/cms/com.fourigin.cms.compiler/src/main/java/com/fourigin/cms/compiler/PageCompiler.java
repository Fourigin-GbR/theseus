package com.fourigin.cms.compiler;

import com.fourigin.cms.models.structure.nodes.PageInfo;

import java.io.OutputStream;

public interface PageCompiler {
    /**
     *
     * @param pageInfo PageInfo with all information about the page to compile.
     * @param out OutputStream where the result should be written.
     * @return Returns the content type of the compiled result.
     */
    String compile(PageInfo pageInfo, OutputStream out);
}

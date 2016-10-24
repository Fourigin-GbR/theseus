package com.fourigin.cms.compiler;

import com.fourigin.cms.models.content.ContentPage;
import com.fourigin.cms.models.structure.CompileState;
import com.fourigin.cms.models.structure.nodes.PageInfo;
import com.fourigin.cms.repository.ContentPageResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: define multiple compilers with different content page resolvers (draft / live)
public class Compiler {
    private final Logger logger = LoggerFactory.getLogger(Compiler.class);

    private ContentPageResolver contentPageResolver;

    public void compile(PageInfo page){
        String pageName = page.getName();

        String pageContentChecksum = page.getChecksum().getCombinedChecksum();

        CompileState compileState = page.getCompileState();
        if(compileState != null){
            if (logger.isDebugEnabled()) logger.debug("Verifying compile state of the page '{}'.", pageName);
            String compileBaseChecksum = compileState.getChecksum();
            if(compileBaseChecksum.equals(pageContentChecksum)){
                if (logger.isInfoEnabled()) logger.info("Skipping page '{}', checksum unchanged.", pageName);
                return;
            }
        }

        if (logger.isInfoEnabled()) logger.info("Compiling page '{}'.", pageName);
        compileState = compileInternal(page);

        long timestamp = System.currentTimeMillis();
        compileState.setTimestamp(timestamp);
        compileState.setChecksum(pageContentChecksum);
        if (logger.isDebugEnabled()) logger.debug("Compile completed with state {}.", compileState);
    }

    private CompileState compileInternal(PageInfo sitePage){
        PageInfo.ContentPageReference contentRef = sitePage.getContentPageReference();

        ContentPage contentPage = contentPageResolver.retrieve(contentRef.getParentPath(), contentRef.getContentId());
        // TODO: implement me!


        return sitePage.getCompileState();
    }

    public void setContentPageResolver(ContentPageResolver contentPageResolver) {
        this.contentPageResolver = contentPageResolver;
    }
}

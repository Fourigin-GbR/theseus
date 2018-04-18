package com.fourigin.argo.compiler;

import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.CompileState;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.repository.ContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: define multiple compilers with different content page resolvers (draft / live)
public class Compiler {
    private final Logger logger = LoggerFactory.getLogger(Compiler.class);

    private ContentRepository contentRepository;

    public void compile(PageInfo page){
        String pageName = page.getName();

        String pageContentChecksum = page.getChecksum().getCombinedValue();

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

    private CompileState compileInternal(PageInfo info){
        ContentPage contentPage = contentRepository.retrieve(info);
        // TODO: implement me!

        if (logger.isDebugEnabled()) logger.debug("contentPage: {}", contentPage);


        return info.getCompileState();
    }

    public void setContentRepository(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }
}

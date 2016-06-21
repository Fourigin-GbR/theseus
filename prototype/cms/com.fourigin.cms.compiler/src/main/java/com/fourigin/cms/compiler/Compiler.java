package com.fourigin.cms.compiler;

import com.fourigin.cms.models.structure.CompileState;
import com.fourigin.cms.models.structure.nodes.SitePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Compiler {
    private final Logger logger = LoggerFactory.getLogger(Compiler.class);

    public void compile(SitePage page){
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
        if (logger.isDebugEnabled()) logger.debug("Compile done, with state {}.", compileState);
    }

    private CompileState compileInternal(SitePage page){
        // TODO: implement me!
        return page.getCompileState();
    }
}

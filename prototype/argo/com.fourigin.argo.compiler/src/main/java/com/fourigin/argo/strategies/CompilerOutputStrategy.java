package com.fourigin.argo.strategies;

import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

import java.io.OutputStream;

public interface CompilerOutputStrategy {
    OutputStream getOutputStream(
        SiteNodeInfo info,
        String filenamePostfix,
        String extension,
        String project,
        String language
    );

    /**
     * This is called by the compiler after a failed operation.
     */
    void reset();

    /**
     * Call this after successful operation you've finished working with the output. This is NOT called by the compiler!
     */
    void finish();
}

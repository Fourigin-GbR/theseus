package com.fourigin.argo.strategies;

import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BufferedCompilerOutputStrategy implements CompilerOutputStrategy {
    private static final ThreadLocal<ByteArrayOutputStream> OUTPUT_CONTAINER_THREAD_LOCAL = new ThreadLocal<>();

    public OutputStream getOutputStream(SiteNodeInfo info, String filenamePostfix, String extension, String project, String language) {
        ByteArrayOutputStream outputStream = OUTPUT_CONTAINER_THREAD_LOCAL.get();
        if (outputStream == null) {
            outputStream = new ByteArrayOutputStream();
            OUTPUT_CONTAINER_THREAD_LOCAL.set(outputStream);
        }
        return outputStream;
    }

    public void reset() {
        OUTPUT_CONTAINER_THREAD_LOCAL.remove();
    }

    /**
     * Call this after successful operation you've finished working with the output. This is NOT called by the compiler!
     */
    public void finish() {
        OUTPUT_CONTAINER_THREAD_LOCAL.remove();
    }


    public String getResult() {
        return new String(getBytes(), StandardCharsets.UTF_8);
    }

    public byte[] getBytes() {
        ByteArrayOutputStream outputStream = OUTPUT_CONTAINER_THREAD_LOCAL.get();
        if (outputStream == null) {
            return null;
        }
        return outputStream.toByteArray();
    }
}

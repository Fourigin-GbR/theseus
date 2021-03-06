package com.fourigin.argo.strategies;

import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileCompilerOutputStrategy implements CompilerOutputStrategy {
    private final Logger logger = LoggerFactory.getLogger(FileCompilerOutputStrategy.class);

    private FilenameStrategy filenameStrategy;

    private DocumentRootResolverStrategy documentRootResolverStrategy;

    private static final ThreadLocal<OutputContainer> OUTPUT_CONTAINER_THREAD_LOCAL =
        new ThreadLocal<OutputContainer>() {
            protected OutputContainer initialValue() {
                return new OutputContainer();
            }
        };

    public void setFilenameStrategy(FilenameStrategy filenameStrategy) {
        this.filenameStrategy = filenameStrategy;
    }

    public void setDocumentRootResolverStrategy(DocumentRootResolverStrategy documentRootResolverStrategy) {
        this.documentRootResolverStrategy = documentRootResolverStrategy;
    }

    public OutputStream getOutputStream(SiteNodeInfo info, String filenamePostfix, String extension, String project, String language) {
        if (logger.isDebugEnabled()) logger.debug(
            "Resolving file output stream for {}/{}, postfix {}, extension {} and {}",
            project,
            language,
            filenamePostfix,
            extension,
            info
        );

        String filename = filenameStrategy.getFilename(language, info);
        if (logger.isDebugEnabled()) logger.debug("filename (from strategy): '{}'", filename);

        if (filenamePostfix != null && !"".equals(filenamePostfix)) {
            filename = filename + filenamePostfix;
        }
        filename = filename + extension;
        if (logger.isDebugEnabled()) logger.debug("filename (after modifications): '{}'", filename);

        String folder = filenameStrategy.getFolder(language, info);
        if (logger.isDebugEnabled()) logger.debug("Folder (from strategy): '{}'", folder);

        String docRoot = documentRootResolverStrategy.resolveDocumentRoot(project, language);
        if (logger.isDebugEnabled()) logger.debug("Document root (from strategy): '{}'", docRoot);

        File docRootFile = new File(docRoot);
        if (docRootFile.mkdirs()) {
            if (logger.isInfoEnabled()) logger.info("Created '{}'.", docRootFile.getAbsolutePath()); // NOPMD
        }
        if (!docRootFile.isDirectory()) {
            throw new IllegalStateException("Document root '" + docRootFile.getAbsolutePath() + "' is not a directory!");
        }

        File nodeRootFile;
        if ("/".equals(folder)) {
            nodeRootFile = docRootFile;
        } else {
            nodeRootFile = new File(docRootFile, folder);
        }

        if (nodeRootFile.mkdirs()) {
            if (logger.isInfoEnabled()) logger.info("Created '{}'.", nodeRootFile.getAbsolutePath()); // NOPMD
        }
        if (!nodeRootFile.isDirectory()) {
            throw new IllegalStateException("Node root '" + nodeRootFile.getAbsolutePath() + "' is not a directory!");
        }

        OutputContainer outputContainer = OUTPUT_CONTAINER_THREAD_LOCAL.get();
        outputContainer.outputFile = new File(nodeRootFile, filename);
        if (logger.isInfoEnabled())
            logger.info("Output file is '{}'.", outputContainer.outputFile.getAbsolutePath());

        try {
            outputContainer.fos = new FileOutputStream(outputContainer.outputFile);
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException("Error opening target file '" + outputContainer.outputFile.getAbsolutePath() + "'!", ex);
        }

        return outputContainer.fos;
    }

    public void reset() {
        OutputContainer outputContainer = OUTPUT_CONTAINER_THREAD_LOCAL.get();
        if (outputContainer.fos != null) {
            try {
                outputContainer.fos.close();
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) logger.error("Unable to close the output stream on reset()", ex);
            }
        }

        if (outputContainer.outputFile != null
            && outputContainer.outputFile.isFile()
            && !outputContainer.outputFile.delete()) {
            if (logger.isErrorEnabled()) // NOPMD
                logger.error("Unable to delete the output file '{}' on reset()!", outputContainer.outputFile.getAbsolutePath()); // NOPMD
        }

        OUTPUT_CONTAINER_THREAD_LOCAL.remove();
    }

    /**
     * Call this after successful operation.
     */
    public void finish() {
        OutputContainer outputContainer = OUTPUT_CONTAINER_THREAD_LOCAL.get();
        if (outputContainer.fos != null) {
            try {
                outputContainer.fos.close();
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) logger.error("Unable to close the output stream on finish()", ex);
            }
        }
        OUTPUT_CONTAINER_THREAD_LOCAL.remove();
    }

    private static class OutputContainer {
        public FileOutputStream fos;
        public File outputFile;
    }
}

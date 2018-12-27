package com.fourigin.argo.compiler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.strategies.FilenameStrategy;
import com.fourigin.argo.template.engine.ProcessingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class DefaultCompilerInterceptor implements CompilerInterceptor {
    private File baseDir;
    private FilenameStrategy preparedContentFilenameStrategy;
    private ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(DefaultCompilerInterceptor.class);

    public DefaultCompilerInterceptor(String preparedContentRoot, String base, FilenameStrategy preparedContentFilenameStrategy) {
        this.preparedContentFilenameStrategy = preparedContentFilenameStrategy;

        File root = new File(preparedContentRoot);
        if(!root.exists()){
            if(!root.mkdirs()){ // NOPMD
                throw new IllegalStateException("Unable to create root directory for prepared-content files '" + root.getAbsolutePath() + "'!");
            }
        }

        baseDir = new File(root, base);
        if(!baseDir.exists()){
            if(!baseDir.mkdirs()){ // NOPMD
                throw new IllegalStateException("Unable to create base directory for prepared-content files '" + baseDir.getAbsolutePath() + "'!");
            }
        }
    }

    @Override
    public void afterPrepareContent(String base, String path, PageInfo pageInfo, ProcessingMode processingMode, ContentPage contentPage) {
//        if(processingMode != ProcessingMode.STAGE) {
//            // only write prepared content in STAGE mode
//            return;
//        }

        String folderPath = preparedContentFilenameStrategy.getFolder(base, pageInfo);
        String filename = preparedContentFilenameStrategy.getFilename(base, pageInfo);

        File folder = new File(baseDir, folderPath);
        if(!folder.exists()){
            if(!folder.mkdirs()){ // NOPMD
                throw new IllegalStateException("Unable to create directory for prepared-content files '" + folder.getAbsolutePath() + "'!");
            }
        }

        File targetFile = new File(folder, filename + ".json");
        try {
            if (logger.isDebugEnabled()) logger.debug("Writing prepared content of {} to {}", pageInfo, targetFile.getAbsolutePath());
            objectMapper.writeValue(targetFile, contentPage);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to write prepared content to file '" + targetFile.getAbsolutePath() + "'!", ex);
        }
    }
}

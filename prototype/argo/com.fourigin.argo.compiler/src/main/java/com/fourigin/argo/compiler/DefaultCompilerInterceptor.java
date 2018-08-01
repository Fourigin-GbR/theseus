package com.fourigin.argo.compiler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.strategies.FilenameStrategy;
import com.fourigin.argo.template.engine.ProcessingMode;

import java.io.File;
import java.io.IOException;

public class DefaultCompilerInterceptor implements CompilerInterceptor {
    private String preparedContentRoot;
    private String base;
    private File baseDir;
    private FilenameStrategy preparedContentFilenameStrategy;
    private ObjectMapper objectMapper = new ObjectMapper();

    public DefaultCompilerInterceptor(String preparedContentRoot, String base, FilenameStrategy preparedContentFilenameStrategy) {
        this.preparedContentRoot = preparedContentRoot;
        this.base = base;
        this.preparedContentFilenameStrategy = preparedContentFilenameStrategy;

        File root = new File(preparedContentRoot);
        if(!root.exists()){
            if(!root.mkdirs()){
                throw new IllegalStateException("Unable to create root directory for prepared-content files '" + root.getAbsolutePath() + "'!");
            }
        }

        baseDir = new File(root, base);
        if(!baseDir.exists()){
            if(!baseDir.mkdirs()){
                throw new IllegalStateException("Unable to create base directory for prepared-content files '" + baseDir.getAbsolutePath() + "'!");
            }
        }
    }

    @Override
    public void afterPrepareContent(String path, PageInfo pageInfo, ProcessingMode processingMode, ContentPage contentPage) {
        if(processingMode != ProcessingMode.STAGE) {
            // only write prepared content in STAGE mode
            return;
        }

        String folderPath = preparedContentFilenameStrategy.getFolder(pageInfo);
        String filename = preparedContentFilenameStrategy.getFilename(pageInfo);

        File folder = new File(baseDir, folderPath);
        if(!folder.exists()){
            if(!folder.mkdirs()){
                throw new IllegalStateException("Unable to create directory for prepared-content files '" + folder.getAbsolutePath() + "'!");
            }
        }

        File targetFile = new File(folder, filename + ".json");
        try {
            objectMapper.writeValue(targetFile, contentPage);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to write prepared content to file '" + targetFile.getAbsolutePath() + "'!", ex);
        }
    }
}

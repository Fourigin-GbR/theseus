package com.fourigin.argo.controller.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.datasource.index.DataSourceIndex;
import com.fourigin.argo.repository.DataSourceIndexResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class LiveDataSourceIndexResolver implements DataSourceIndexResolver {
    private static final String PAGE_INDEX_FILE_POSTFIX = "_index";

    private ObjectMapper objectMapper;

    private String contentRoot;

    private final Logger logger = LoggerFactory.getLogger(LiveDataSourceIndexResolver.class);

    public LiveDataSourceIndexResolver(String contentRoot, ObjectMapper objectMapper) {
        Objects.requireNonNull(contentRoot, "contentRoot must not be null!");
        Objects.requireNonNull(objectMapper, "objectMapper must not be null!");

        this.contentRoot = contentRoot;
        this.objectMapper = objectMapper;
    }

    @Override
    public DataSourceIndex resolveIndex(String path, String indexName) {
        File hiddenDir = new File(getContentRoot(), ".index");
        if (!hiddenDir.exists()) { // NOPMD
            if (!hiddenDir.mkdirs()) { // NOPMD
                throw new IllegalStateException("Unable to create missing hidden directory '" + hiddenDir.getAbsolutePath() + "'!");
            }
        }

        File indexFile = getIndexFile(hiddenDir, path, indexName);

        try (InputStream is = new BufferedInputStream(new FileInputStream(indexFile))) {
            return objectMapper.readValue(is, DataSourceIndex.class);
        } catch (IOException ex) {
            if (logger.isErrorEnabled())
                logger.error("Error reading index file ({})!", indexFile.getAbsolutePath(), ex);
            throw new IllegalStateException("Unable to read index file (" + indexFile.getAbsolutePath() + ")", ex);
        }
    }

    File getIndexFile(File rootDir, String path, String indexName) {
        int pos = path.lastIndexOf('/');
        String pageName = path.substring(pos + 1);
        String folderName = path.substring(0, pos);

        File dir = new File(rootDir, folderName);
        String fileName = pageName + '_' + indexName + PAGE_INDEX_FILE_POSTFIX + ".json";

        return new File(dir, fileName);
    }

    File getContentRoot() {
        File rootDirectory = new File(contentRoot);

        if (!rootDirectory.exists()) {
            if (logger.isDebugEnabled())
                logger.debug("Creating a missing root directory '{}'.", rootDirectory.getAbsolutePath());

            //noinspection ResultOfMethodCallIgnored
            rootDirectory.mkdirs();
        }
        if (!rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("root '" + rootDirectory.getAbsolutePath() + "' is not a directory!");
        }

        if (logger.isDebugEnabled()) logger.debug("Content-root: '{}'", rootDirectory.getAbsolutePath());
        return rootDirectory;
    }
}

package com.fourigin.cms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.cms.models.content.ContentPage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonFileContentPageRepository implements ContentPageRepository {
    private String contentRoot;

    private ObjectMapper objectMapper;

    private ConcurrentHashMap<String, ReadWriteLock> locks = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(JsonFileContentPageRepository.class);

    @Override
    public ContentPage retrieve(String parentPath, String id) {
        Objects.requireNonNull(parentPath, "Parent path must not be null!");
        Objects.requireNonNull(id, "ID of the content-page must not be null!");

        if (logger.isDebugEnabled())
            logger.debug("Retrieving ContentPage for parent '{}' & id '{}')", parentPath, id);

        ReadWriteLock lock = getLock(parentPath + '/' + id);
        lock.readLock().lock();

        try {
            File contentFile = getContentFile(parentPath, id);
            String fullPath = contentFile.getAbsolutePath();
            if (!contentFile.exists()) {
                if (logger.isInfoEnabled())
                    logger.info("Content file {} does not exist!", fullPath);

                return null;
            }

            return readContentPage(id, contentFile);
        }
        finally
        {
            lock.readLock().unlock();
        }
    }

    @Override
    public void create(String parentPath, ContentPage contentPage) {
        Objects.requireNonNull(parentPath, "Parent path must not be null!");
        Objects.requireNonNull(contentPage, "Content-page must not be null!");

        String id = contentPage.getId();
        if (logger.isDebugEnabled())
            logger.debug("Creating ContentPage for parent '{}' with id '{}')", parentPath, id);

        ReadWriteLock lock = getLock(parentPath + '/' + id);
        lock.writeLock().lock();

        try {
            File contentFile = getContentFile(parentPath, id);
            if (contentFile.exists()) {
                if (logger.isErrorEnabled())
                    logger.error("Content file {} does already exist!", contentFile.getAbsolutePath());

                return;
            }

            writeContentPage(id, contentPage, contentFile);
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void update(String parentPath, ContentPage contentPage) {
        Objects.requireNonNull(parentPath, "Parent path must not be null!");
        Objects.requireNonNull(contentPage, "Content-page must not be null!");

        String id = contentPage.getId();
        if (logger.isDebugEnabled())
            logger.debug("Updating ContentPage for parent '{}' with id '{}')", parentPath, id);

        ReadWriteLock lock = getLock(parentPath + '/' + id);
        lock.writeLock().lock();

        try {
            File contentFile = getContentFile(parentPath, id);
            if (!contentFile.exists()) {
                if (logger.isErrorEnabled())
                    logger.error("Content file {} does not exist!", contentFile.getAbsolutePath());

                return;
            }

            writeContentPage(id, contentPage, contentFile);
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(String parentPath, String id) {
        Objects.requireNonNull(parentPath, "Parent path must not be null!");
        Objects.requireNonNull(id, "ID of the content-page must not be null!");

        if (logger.isDebugEnabled())
            logger.debug("Deleting ContentPage for parent '{}' & id '{}')", parentPath, id);

        ReadWriteLock lock = getLock(parentPath + '/' + id);
        lock.writeLock().lock();

        try {
            File contentFile = getContentFile(parentPath, id);
            if (!contentFile.exists()) {
                if (logger.isErrorEnabled())
                    logger.error("Content file {} does not exist!", contentFile.getAbsolutePath());

                return;
            }

            boolean deleted = contentFile.delete();
            if(!deleted){
                throw new IllegalStateException("Unable to delete content-file '" + contentFile.getAbsolutePath() + "'!");
            }
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    private ContentPage readContentPage(String id, File contentFile){
        InputStream is = null;
        ContentPage result = null;
        try
        {
            is = new BufferedInputStream(new FileInputStream(contentFile));
            result = objectMapper.readValue(is, ContentPage.class);
        }
        catch(IOException ex)
        {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error loading content for id '" + id + "' (" + contentFile.getAbsolutePath() + ")!", ex);
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }

        if(result == null)
        {
            if(logger.isWarnEnabled())
                logger.warn("Error reading content file '{}'", contentFile.getAbsolutePath());

            return null;
        }

        result.setId(id); // why?

        return result;
    }

    private void writeContentPage(String id, ContentPage contentPage, File contentFile){
        OutputStream os = null;
        try
        {
            os = new BufferedOutputStream(new FileOutputStream(contentFile));
            objectMapper.writeValue(os, contentPage);
        }
        catch(IOException ex)
        {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing content for id '" + id + "' (" + contentFile.getAbsolutePath() + ")!", ex);
        }
        finally
        {
            IOUtils.closeQuietly(os);
        }
    }

    private File getContentFile(String parentPath, String contentId) {
        File contentRootFile = new File(contentRoot);

        //noinspection ResultOfMethodCallIgnored
        contentRootFile.mkdirs();
        if (!contentRootFile.isDirectory()) {
            throw new IllegalArgumentException("contentRoot '" + contentRootFile.getAbsolutePath() + "' is not a directory!");
        }

        String filename = contentId + ".json";

        // remove first '/'
        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }

        // replace all '/' with '_'
        filename = filename.replace('/', '_');

        File directory = new File(contentRootFile, parentPath);

        File contentFile = new File(directory, filename);

        if(logger.isTraceEnabled()) logger.trace("Resolved content file '{}'", contentFile.getAbsolutePath());

        return contentFile;
    }

    private ReadWriteLock getLock(String id) {
        ReadWriteLock result = locks.get(id);
        if(result != null){
            return result;
        }

        if (logger.isInfoEnabled())
            logger.info("Creating new lock for id '{}'", id);

        result = new ReentrantReadWriteLock();
        locks.putIfAbsent(id, result);
        result = locks.get(id); // to be sure it's really the correct lock...

        return result;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setContentRoot(String contentRoot) {
        this.contentRoot = contentRoot;
    }

    public String getContentRoot() {
        return contentRoot;
    }

    public static class Builder {
        private String contentRoot;
        private ObjectMapper objectMapper;

        public Builder contentRoot(String path){
            this.contentRoot = path;
            return this;
        }

        public Builder objectMapper(ObjectMapper objectMapper){
            this.objectMapper = objectMapper;
            return this;
        }

        public JsonFileContentPageRepository build(){
            JsonFileContentPageRepository instance = new JsonFileContentPageRepository();
            instance.setObjectMapper(objectMapper);
            instance.setContentRoot(contentRoot);
            return instance;
        }
    }
}

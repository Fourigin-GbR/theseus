package com.fourigin.utilities.core;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.concurrent.locks.ReadWriteLock;

public abstract class JsonFileBasedRepository extends FileBasedRepository {
    private ObjectMapper objectMapper;

    private File baseDirectory;

    private final Logger logger = LoggerFactory.getLogger(JsonFileBasedRepository.class);

    protected abstract <T> File getFile(Class<T> target, String id, String mimeType, String... path);

    protected <T> T read(Class<T> target, String id, String... path) {
        File file = getFile(target, id, "application/json", path);
        if (logger.isDebugEnabled())
            logger.debug("Trying to read {} data from file {}", target.getName(), file.getAbsolutePath());
        if (!file.exists()) {
            if (logger.isDebugEnabled()) logger.debug("No file found: {}", file.getAbsolutePath());
            return null;
        }

        ReadWriteLock lock = getLock(id);
        lock.readLock().lock();

        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            return objectMapper.readValue(is, target);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error reading data from file (" + file.getAbsolutePath() + ")!", ex);
        } finally {
            lock.readLock().unlock();
        }
    }

    protected <T> void write(T data, String id, String... path) {
        File file = getFile(data.getClass(), id, "application/json", path);
        if (logger.isDebugEnabled())
            logger.debug("Writing data to file {}", file.getAbsolutePath());

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, data);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing data to file (" + file.getAbsolutePath() + ")!", ex);
        }
    }

    public static String resolveBasePath(String id) {
        String firstBlobPart = id.substring(0, 2);
        String remainingBlobPart = id.substring(2);
        return firstBlobPart + "/" + remainingBlobPart;
    }

    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public File getBaseDirectory() {
        return baseDirectory;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}

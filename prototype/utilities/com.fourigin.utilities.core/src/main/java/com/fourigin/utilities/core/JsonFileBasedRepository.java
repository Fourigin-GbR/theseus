package com.fourigin.utilities.core;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    abstract protected <T> File getDataFileBase(Class<T> target, String id, String... path);
    abstract protected <T> String getDataFileName(Class<T> target, String... path);

    protected <T> File getFile(Class<T> target, String id, String... path) {
//        File directory = new File(baseDirectory, resolveBasePath(id));
        File directory = getDataFileBase(target, id, path);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Unable to create missing directory '" + directory.getAbsolutePath() + "'!");
        }

        String propsFile = getDataFileName(target, path) + ".json";
        return new File(directory, propsFile);
    }

//    File getPropsFile(String base, String assetId) {
//        String assetBase = DIR_META_BASE + "/" + Assets.resolveAssetBasePath(assetId);
//        File assetDirectory = new File(baseDirectory, assetBase);
//
//        if (!assetDirectory.exists() && !assetDirectory.mkdirs()) {
//            throw new IllegalStateException("Unable to create missing asset directory '" + assetDirectory.getAbsolutePath() + "'!");
//        }
//
//        String propsFile = "props_" + base + ".json";
//        return new File(assetDirectory, propsFile);
//    }

    protected <T> T read(Class<T> target, String id, String... path) {
        File file = getFile(target, id, path);
        if (!file.exists()) {
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

    protected <T> void write(T data, String id,  String... path) {
        File file = getFile(data.getClass(), id, path);

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, data);
        } catch (IOException ex) {
            // TODO: create proper exception handling
            throw new IllegalArgumentException("Error writing data to file (" + file.getAbsolutePath() + ")!", ex);
        }
    }

    public static String resolveBasePath(String id){
        String firstBlobPart = id.substring(0,2);
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

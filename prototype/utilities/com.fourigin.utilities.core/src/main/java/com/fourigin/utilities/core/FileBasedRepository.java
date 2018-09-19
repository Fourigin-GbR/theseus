package com.fourigin.utilities.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileBasedRepository {

    private final Logger logger = LoggerFactory.getLogger(FileBasedRepository.class);

    private ConcurrentHashMap<String, ReadWriteLock> locks = new ConcurrentHashMap<>();

    protected ReadWriteLock getLock(String id) {
        ReadWriteLock result = locks.get(id);
        if (result != null) {
            return result;
        }

        if (logger.isInfoEnabled())
            logger.info("Creating new lock for id '{}'", id);

        result = new ReentrantReadWriteLock();
        locks.putIfAbsent(id, result);
        result = locks.get(id); // to be sure it's really the correct lock...

        return result;
    }
}

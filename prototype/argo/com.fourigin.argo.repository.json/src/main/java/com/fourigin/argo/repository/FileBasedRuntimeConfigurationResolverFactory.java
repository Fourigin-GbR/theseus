package com.fourigin.argo.repository;

import com.fourigin.utilities.core.PropertiesReplacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class FileBasedRuntimeConfigurationResolverFactory implements RuntimeConfigurationResolverFactory {

    private String basePath;

    private String keyName;

    private PropertiesReplacement propertiesReplacement = new PropertiesReplacement("\\[(.+?)\\]");

    private ConcurrentHashMap<String, RuntimeConfigurationResolver> cache = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(FileBasedRuntimeConfigurationResolverFactory.class);

    @Override
    public RuntimeConfigurationResolver getInstance(String customer, String key) {
        RuntimeConfigurationResolver resolver = cache.get(key);
        if(resolver != null){
            if (logger.isDebugEnabled()) logger.debug("Using cached RuntimeConfigurationResolver instance for key '{}'.", key);
            return resolver;
        }

        String path = propertiesReplacement.process(basePath, keyName, key);

        if (logger.isDebugEnabled()) logger.debug("Instantiating a new RuntimeConfigurationResolver instance for key '{}' and path '{}'.", key, path);
        resolver = new FileBasedRuntimeConfigurationResolver(customer, path);

        cache.putIfAbsent(key, resolver);

        return resolver;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}

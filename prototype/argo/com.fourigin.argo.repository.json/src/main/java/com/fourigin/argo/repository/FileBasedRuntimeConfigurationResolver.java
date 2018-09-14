package com.fourigin.argo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.content.config.RuntimeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class FileBasedRuntimeConfigurationResolver implements RuntimeConfigurationResolver {
    private static final String CONFIG_DIRECTORY_NAME = ".config";

    private String basePath;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(FileBasedRuntimeConfigurationResolver.class);

    public FileBasedRuntimeConfigurationResolver(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public Set<String> listAvailableConfigurations() {
        Set<String> configNames = new HashSet<>();

        File base = getConfigDirectory();
        if (!base.exists() || !base.canRead()) {
            if (logger.isWarnEnabled())
                logger.warn("Unable to read base directory for runtime configurations '{}'!", base.getAbsolutePath());
            return configNames;
        }

        File[] configFiles = base.listFiles((dir, name) -> name.startsWith("config_") && name.endsWith(".json"));
        if (configFiles == null || configFiles.length == 0) {
            if (logger.isInfoEnabled())
                logger.info("No runtime configuration files found in '{}'.", base.getAbsolutePath());
            return configNames;
        }

        int startPos = "config_".length();
        for (File configFile : configFiles) {
            String fileName = configFile.getName();
            int endPos = fileName.indexOf(".json");
            String configName = fileName.substring(startPos, endPos);
            if (!configName.isEmpty()) {
                if (logger.isDebugEnabled())
                    logger.debug("Detected a config name '{}' from file '{}'", configName, fileName);
                configNames.add(configName);
            }
        }

        return configNames;
    }

    @Override
    public RuntimeConfiguration resolveConfiguration(String name) {
        File base = getConfigDirectory();

        String fileName = "config_" + name + ".json";
        File configFile = new File(base, fileName);
        if (!configFile.exists() || !configFile.canRead()) {
            if (logger.isErrorEnabled()) logger.error("Unable to read config file '{}'!", configFile.getAbsolutePath());
            return null;
        }

        try (InputStream is = new BufferedInputStream(new FileInputStream(configFile))) {
            return objectMapper.readValue(is, RuntimeConfiguration.class);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error reading runtime configuration file (" + configFile.getAbsolutePath() + ")!", ex);
        }
    }

    /* private -> testing */
    File getConfigDirectory() {
        File rootDirectory = new File(basePath);

        File configDirectory = new File(rootDirectory, CONFIG_DIRECTORY_NAME);

        String path = configDirectory.getAbsolutePath();

        if (!configDirectory.exists()) {
            if (logger.isDebugEnabled()) logger.debug("Creating a missing CONFIG directory '{}'.", path);

            //noinspection ResultOfMethodCallIgnored
            configDirectory.mkdirs();
        }
        if (!configDirectory.isDirectory()) {
            throw new IllegalArgumentException("CONFIG '" + path + "' is not a directory!");
        }

        if (logger.isDebugEnabled())
            logger.debug("Resolved CONFIG directory to '{}'", path);

        return configDirectory;
    }

}

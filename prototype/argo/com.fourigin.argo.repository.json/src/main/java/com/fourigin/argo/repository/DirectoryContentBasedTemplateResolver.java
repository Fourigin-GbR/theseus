package com.fourigin.argo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.ChecksumGenerator;
import com.fourigin.argo.models.content.ContentPagePrototype;
import com.fourigin.argo.models.template.Template;
import com.fourigin.argo.models.template.TemplateVariation;
import com.fourigin.argo.models.template.Type;
import com.fourigin.argo.projects.ProjectSpecificPathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DirectoryContentBasedTemplateResolver implements TemplateResolver {
    private String templateBasePath;

    private ObjectMapper objectMapper;

    private ProjectSpecificPathResolver pathResolver;

    private final Logger logger = LoggerFactory.getLogger(DirectoryContentBasedTemplateResolver.class);

    public DirectoryContentBasedTemplateResolver(String templateBasePath, ObjectMapper objectMapper, ProjectSpecificPathResolver pathResolver) {
        this.templateBasePath = templateBasePath;
        this.objectMapper = objectMapper;
        this.pathResolver = pathResolver;
    }

    @Override
    public Template retrieve(String projectId, String id) {
        Objects.requireNonNull(id, "Template id must not be null!");

        if (logger.isDebugEnabled()) logger.debug("Retrieving template for project '{}' and id '{}' ...", projectId, id);

        File baseDir = new File(pathResolver.resolvePath(templateBasePath, projectId));
        String templateReference = id;
        int pos = templateReference.indexOf('.');
        while(pos >= 0){
            String subDir = templateReference.substring(0, pos);
            templateReference = templateReference.substring(pos+1);
            baseDir = new File(baseDir, subDir);
            pos = templateReference.indexOf('.');
        }

        final String templateBaseName = templateReference;
        File[] matchingFiles = baseDir.listFiles((dir, name) -> name.startsWith(templateBaseName) && name.endsWith(".html"));

        Map<String, String> checksums = new HashMap<>();

        // read all available variation files
        Set<TemplateVariation> variations = new HashSet<>();
        if(matchingFiles != null && matchingFiles.length > 0){
            if (logger.isDebugEnabled()) logger.debug("Found matching files: {}", Arrays.asList(matchingFiles));

            for (File matchingFile : matchingFiles) {
                String fileName = matchingFile.getName();
                String variationName = TemplateVariation.DEFAULT_VARIATION_NAME;
                int dot = fileName.indexOf(".html");
                int separator = fileName.indexOf('#');
                if(separator >= 0){
                    variationName = fileName.substring(separator+1, dot);
                }

                TemplateVariation variation = new TemplateVariation();
                variation.setId(variationName);
                variation.setType(Type.THYMELEAF);
                variation.setOutputContentType("text/html");
                variations.add(variation);

                String variationChecksum = ChecksumGenerator.getChecksum(variation);
                if (logger.isDebugEnabled()) logger.debug("Added a variation '{}' ({})", variationName, variationChecksum);

                checksums.put(fileName, variationChecksum);
            }
        }

        // build the template's revision
        List<String> keys = new ArrayList<>(checksums.keySet());
        Collections.sort(keys);
        StringBuilder revision = new StringBuilder();
        for (String key : keys) {
            if(revision.length() > 0){
                revision.append('-');
            }
            revision.append(checksums.get(key));
        }
        String templateRevision = revision.toString();
        if (logger.isDebugEnabled()) logger.debug("Template revision: '{}'", templateRevision);

        Template template = new Template();
        template.setId(id);
        template.setRevision(templateRevision);
        template.setVariations(variations);

        // read prototype, if available
        File prototypeFile = new File(baseDir, templateBaseName + ".json");
        if (logger.isDebugEnabled()) logger.debug("Searching for prototype file '{}'", prototypeFile.getAbsolutePath());
        if(prototypeFile.exists()){
            try(InputStream is = new BufferedInputStream(new FileInputStream(prototypeFile))) {
                ContentPagePrototype prototype = objectMapper.readValue(is, ContentPagePrototype.class);
                template.setPrototype(prototype);
                if (logger.isDebugEnabled()) logger.debug("Content page prototype provided.");
            }
            catch(Throwable ex)
            {
                // TODO: create proper exception handling
                throw new IllegalArgumentException("Error loading content prototype for id '" + id + "' (" + prototypeFile.getAbsolutePath() + ")!", ex);
            }
        }

        return template;
    }

    @Override
    public Set<Template> list(String projectId) {
        if (logger.isDebugEnabled()) logger.debug("Listing templates for project '{}' ...", projectId);

        File baseDir = new File(pathResolver.resolvePath(templateBasePath, projectId));
        if (logger.isDebugEnabled()) logger.debug("baseDir: '{}'", baseDir);

        // TODO: implement customer specific template packages
        return null;
    }
}

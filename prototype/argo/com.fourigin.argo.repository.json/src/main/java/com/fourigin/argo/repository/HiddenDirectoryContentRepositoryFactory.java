package com.fourigin.argo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.ChecksumGenerator;
import com.fourigin.argo.models.structure.nodes.SiteNodeContainerInfo;
import com.fourigin.argo.projects.ProjectSpecificPathResolver;
import com.fourigin.argo.repository.strategies.PageInfoTraversingStrategy;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class HiddenDirectoryContentRepositoryFactory implements ContentRepositoryFactory {

    private String basePath;

    private String baseTempPath;

    private PageInfoTraversingStrategy defaultTraversingStrategy;

    private ProjectSpecificPathResolver pathResolver;

    private ObjectMapper objectMapper;

    private ConcurrentHashMap<String, HiddenDirectoryContentRepository> cache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, HiddenDirectoryContentRepository> transactions = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(HiddenDirectoryContentRepositoryFactory.class);

    public HiddenDirectoryContentRepositoryFactory() {
        try {
            baseTempPath = File.createTempFile("transactional-base", getClass().getName()).getPath();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create a temp directory!", ex);
        }
    }

    @Override
    public ContentRepository getInstance(String projectId, String language) {
        String path = pathResolver.resolvePath(basePath, projectId, language);
        String masterKey = buildRepoKey(projectId, language);
        return resolveRepository(masterKey, path);
    }

    @Override
    public ContentRepository getInstanceForTransaction(String projectId, String language) {
        String path = pathResolver.resolvePath(basePath, projectId, language);
        String masterKey = buildRepoKey(projectId, language);
        HiddenDirectoryContentRepository masterRepository = resolveRepository(masterKey, path);

        SiteNodeContainerInfo root = masterRepository.getRoot();
        String masterChecksum = ChecksumGenerator.getChecksum(root);

        for (String key : transactions.keySet()) {
            if (key.startsWith(masterKey)) {
                throw new IllegalStateException("A transaction for master key '" + masterKey + "' already exist!");
            }
        }

        String currentPath = baseTempPath + "/${project}_${language}";
        String transactionalPath = pathResolver.resolvePath(currentPath, projectId, language);
        File currentBaseDirectory = new File(transactionalPath);

        // copy files from master to transaction
        File masterRootDirectory = masterRepository.getContentRoot();
        try {
            FileUtils.copyDirectory(masterRootDirectory, currentBaseDirectory);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to prepare transactional repository!", ex);
        }

        // init the new transactional repository
        HiddenDirectoryContentRepository repo = resolveRepository(masterKey, currentBaseDirectory.getAbsolutePath());

        // store it to all transactions
        String transactionKey = buildRepoKey(projectId, language, masterChecksum);
        transactions.put(transactionKey, repo);

        return repo;
    }

    @Override
    public void commitChanges(String projectId, String language, ContentRepository repo) {
        String repoKey = repo.getId();
        String transactionalKey = buildRepoKey(projectId, language, repoKey);
        HiddenDirectoryContentRepository entry = transactions.get(transactionalKey);
        if (entry == null) {
            throw new IllegalArgumentException("No transaction available for key '" + transactionalKey + "'!");
        }

        String path = pathResolver.resolvePath(basePath, projectId, language);
        String masterKey = buildRepoKey(projectId, language);
        HiddenDirectoryContentRepository masterRepository = resolveRepository(masterKey, path);
        String masterId = masterRepository.getId();
        String transactionId = entry.getId();
        if (!masterId.equals(transactionId)) {
            throw new IllegalStateException("Master and transaction have different IDs (" + masterId + ", " + transactionId + "). Commit not possible!");
        }

        // copy files from transaction to master
        File masterRootDirectory = masterRepository.getContentRoot();
        File transactionRootDirectory = entry.getContentRoot();
        try {
            FileUtils.copyDirectory(transactionRootDirectory, masterRootDirectory);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to commit transactional repository!", ex);
        }

        // remove transactional files
        transactionRootDirectory.deleteOnExit();
        transactions.remove(transactionalKey);
    }

    @Override
    public void rollbackChanges(String projectId, String language, ContentRepository repo) {
        String repoKey = repo.getId();
        String transactionalKey = buildRepoKey(projectId, language, repoKey);
        HiddenDirectoryContentRepository entry = transactions.get(transactionalKey);
        if (entry == null) {
            throw new IllegalArgumentException("No transaction available for key '" + transactionalKey + "'!");
        }

        // remove transactional files
        File transactionRootDirectory = entry.getContentRoot();
        transactionRootDirectory.deleteOnExit();
        transactions.remove(transactionalKey);
    }

    private String buildRepoKey(String... key) {
        Objects.requireNonNull(key, "key must not be null!");
        return String.join(":", key);
    }

    private HiddenDirectoryContentRepository resolveRepository(String key, String basePath) {
        HiddenDirectoryContentRepository repository = cache.get(key);
        if (repository != null) {
            if (logger.isDebugEnabled())
                logger.debug("Using cached ContentRepository instance for key '{}'.", key);
            return repository;
        }

        if (logger.isDebugEnabled())
            logger.debug("Instantiating a new ContentRepository instance for key '{}' and path '{}'.", key, basePath);
        repository = new HiddenDirectoryContentRepository();


        repository.setContentRoot(basePath);
        repository.setDefaultTraversingStrategy(defaultTraversingStrategy);
        repository.setObjectMapper(objectMapper);

        cache.putIfAbsent(key, repository);

        return repository;
    }

    public void setPathResolver(ProjectSpecificPathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setDefaultTraversingStrategy(PageInfoTraversingStrategy defaultTraversingStrategy) {
        this.defaultTraversingStrategy = defaultTraversingStrategy;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}

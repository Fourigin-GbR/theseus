package com.fourigin.argo.assets.processor;

import com.fourigin.argo.assets.models.Asset;
import com.fourigin.argo.assets.models.Assets;
import com.fourigin.argo.assets.repository.AssetResolver;
import com.fourigin.argo.compiler.processor.ContentPageProcessor;
import com.fourigin.argo.forms.config.CustomerSpecificConfiguration;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.ContentPageManager;
import com.fourigin.argo.models.content.elements.ObjectAwareContentElement;
import com.fourigin.argo.models.structure.nodes.PageInfo;
import com.fourigin.argo.template.engine.ProcessingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AssetsContentPageProcessor implements ContentPageProcessor {
    private AssetResolver assetResolver;

    private CustomerSpecificConfiguration customerSpecificConfiguration;

//    private List<File> loadBalancerDocumentRoots;

//    private String assetsDomain;

    private final ExecutorService executorService = Executors.newFixedThreadPool(8); // 32?
    private final CompletionService<FileCopyResult> completionService = new ExecutorCompletionService<>(executorService);

    private final Logger logger = LoggerFactory.getLogger(AssetsContentPageProcessor.class);

    @Override
    public void process(String customer, String base, PageInfo info, ProcessingMode processingMode, ContentPage page) {
        String pageId = page.getId();

        if (logger.isInfoEnabled()) logger.info("Processing assets of page {}", pageId);

        List<ObjectAwareContentElement> objectElements = ContentPageManager.collect(page, ObjectAwareContentElement.class);
        if (objectElements == null) {
            if (logger.isInfoEnabled()) logger.info("No object aware elements found in the content page {}", pageId);
            return;
        }

        Collection<ObjectAwareContentElement> elementsToProcess = new HashSet<>();

        Collection<String> assetIds = new HashSet<>();
        for (ObjectAwareContentElement objectElement : objectElements) {
            String assetId = objectElement.getReferenceId();

            if (assetId != null && !assetId.isEmpty()) {
                if (logger.isDebugEnabled()) logger.debug("Found an asset-id {}", assetId);
                assetIds.add(assetId);
                elementsToProcess.add(objectElement);
            }
        }

        if (assetIds.isEmpty()) {
            if (logger.isInfoEnabled())
                logger.info("No object aware elements found with reference-id in the content page {}", pageId);
            return;
        }

        if (logger.isDebugEnabled()) logger.debug("customer specific configuration: {}", customerSpecificConfiguration);

        String assetsDomain = customerSpecificConfiguration.getAssetsDomain().get(customer);
        if(!assetsDomain.endsWith("/")){
            assetsDomain += "/";
        }

        Map<String, Asset> assets = assetResolver.retrieveAssets(base, assetIds);
        Map<String, String> resolvedAssetPaths = new HashMap<>();

        Set<String> unresolvedIds = new HashSet<>();
        for (ObjectAwareContentElement objectElement : elementsToProcess) {
            String assetId = objectElement.getReferenceId();
            Asset asset = assets.get(assetId);
            if (asset == null) {
                unresolvedIds.add(assetId);
                continue;
            }

            String source = resolveAssetLink(base, asset);
            resolvedAssetPaths.put(assetId, source);

            // set element properties
            objectElement.setMimeType(asset.getMimeType());
            objectElement.setSource(assetsDomain + source);
        }

        if (!unresolvedIds.isEmpty()) {
            if (logger.isWarnEnabled())
                logger.warn("Unresolved asset-ids: {}", unresolvedIds);

            assetIds.removeAll(unresolvedIds);
        }

        if (processingMode == ProcessingMode.STAGE) {
            if (logger.isDebugEnabled()) logger.debug("Transferring assets {} based on {}", assetIds, assets);

            Set<Asset> assetsToTransfer = new HashSet<>();
            for (Asset asset : assets.values()) {
                if (assetIds.contains(asset.getId())) {
                    assetsToTransfer.add(asset);
                }
            }
            transferResources(customer, assetsToTransfer, resolvedAssetPaths);
        }
    }

    private String resolveAssetLink(String base, Asset asset) {
        Objects.requireNonNull(asset, "asset must not be null!");

        String id = asset.getId();
        String basePath = Assets.resolveAssetBasePath(id);
        String filename = Assets.resolveAssetFileName(base, asset);

        return basePath + "/" + filename;
    }

    private void transferResources(String customer, Collection<Asset> assets, Map<String, String> resourcePathMapping) {
        String loadBalancerDocumentRootPaths = customerSpecificConfiguration.getAssetsLoadBalancerRoot().get(customer);
        List<File> loadBalancerDocumentRoots = new ArrayList<>();
        for(String path : loadBalancerDocumentRootPaths.split(",")){
            loadBalancerDocumentRoots.add(new File(path.trim()));
        }

        int maxValue = loadBalancerDocumentRoots.size() * assets.size();

        FileCopyResultRunnable runnable = new FileCopyResultRunnable(maxValue);
        Thread t = new Thread(runnable);
        t.start();

        if (logger.isDebugEnabled()) logger.debug("Start transferring assets ...");

        for (File documentRoot : loadBalancerDocumentRoots) {
            String docRootPath = documentRoot.getAbsolutePath();
            if (documentRoot.mkdirs()) {
                if (logger.isDebugEnabled()) logger.debug("Created directory '{}'.", docRootPath); // NOPMD
            }

            for (Asset asset : assets) {
                String resourceId = asset.getId();
                String path = resourcePathMapping.get(resourceId);
                completionService.submit(new FileCopyCallable(resourceId, docRootPath, path));
            }
        }

        try {
            t.join();
        } catch (InterruptedException e) {
            if (logger.isErrorEnabled()) logger.error("Interrupted!", e);
        }
    }

//    public void setLoadBalancerDocumentRoots(List<File> loadBalancerDocumentRoots) {
//        this.loadBalancerDocumentRoots = loadBalancerDocumentRoots;
//    }

//    public void setAssetsDomain(String assetsDomain) {
//        Objects.requireNonNull(assetsDomain, "assetDomain must not be null!");
//
//        if (!assetsDomain.endsWith("/")) {
//            assetsDomain += "/";
//        }
//
//        this.assetsDomain = assetsDomain;
//    }

    public void setAssetResolver(AssetResolver assetResolver) {
        this.assetResolver = assetResolver;
    }

    public void setCustomerSpecificConfiguration(CustomerSpecificConfiguration customerSpecificConfiguration) {
        this.customerSpecificConfiguration = customerSpecificConfiguration;
    }

    private class FileCopyResultRunnable implements Runnable {
        private int count;

        FileCopyResultRunnable(int count) {
            this.count = count;
        }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                try {
                    Future<FileCopyResult> future = completionService.take();
                    future.get();
                } catch (InterruptedException | ExecutionException ex) {
                    if (logger.isErrorEnabled())
                        logger.error("Error occurred!", ex);
                }
            }

            if (logger.isDebugEnabled()) logger.debug("Finished transferring resources.");
        }
    }

    private static class FileCopyResult {
        private final String blobId;
        private final String path;

        FileCopyResult(String blobId, String path) {
            this.blobId = blobId;
            this.path = path;
        }

        public String getBlobId() {
            return blobId;
        }

        public String getPath() {
            return path;
        }
    }

    private class FileCopyCallable implements Callable<FileCopyResult> {

        private final String assetId;
        private final String docRootPath;
        private final String path;

        FileCopyCallable(String assetId, String docRootPath, String path) {
            this.assetId = assetId;
            this.docRootPath = docRootPath;
            this.path = path;
        }

        @Override
        public FileCopyResult call() {
            try {
                long sizeOfBlob = assetResolver.sizeOfAssetData(assetId);
                if (sizeOfBlob < 0) {
                    if (logger.isWarnEnabled())
                        logger.warn("Asset {} doesn't contain blob data!", assetId);
                    return null;
                }

                Path resourcePath = Paths.get(docRootPath, path);
                File resourceFile = resourcePath.toFile();

                if (sizeOfBlob == resourceFile.length()) {
                    if (logger.isDebugEnabled())
                        logger.debug("Skipping existing file '{}' for resource {}.", resourceFile.getAbsolutePath(), assetId);
                    return new FileCopyResult(assetId, path);
                }

                try (InputStream is = assetResolver.retrieveAssetData(assetId)) {
                    if (is == null) {
                        if (logger.isErrorEnabled())
                            logger.error("Could not resolve input stream for blob id {}!", assetId);
                        return null;
                    }

                    Path parentPath = resourcePath.getParent();
                    Files.createDirectories(parentPath);
                    long bytes = Files.copy(is, resourcePath, StandardCopyOption.REPLACE_EXISTING);
                    if (logger.isInfoEnabled())
                        logger.info("Wrote {} bytes to file '{}' for resource {}.", bytes, resourceFile.getAbsolutePath(), assetId);

                    if (logger.isDebugEnabled())
                        logger.debug("Adding transferred entry {}:{} to the cache", assetId, path);
                } catch (IOException e) {
                    if (logger.isErrorEnabled())
                        logger.error("Exception while transferring resource {} to file '{}'!", assetId, resourceFile.getAbsolutePath(), e);
                }
            } catch (Throwable ex) {
                if (logger.isWarnEnabled())
                    logger.warn("Unexpected error occurred!", ex);
                return null;
            }

            return new FileCopyResult(assetId, path);
        }
    }
}

package com.fourigin.argo.assets.repository;

import com.fourigin.argo.assets.models.Asset;
import com.fourigin.argo.assets.models.AssetFactory;
import com.fourigin.argo.assets.models.AssetProperties;
import com.fourigin.utilities.core.JsonFileBasedRepository;
import de.huxhorn.sulky.blobs.AmbiguousIdException;
import de.huxhorn.sulky.blobs.impl.BlobRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BlobBasedAssetRepository extends JsonFileBasedRepository implements AssetRepository {
    private final Logger logger = LoggerFactory.getLogger(BlobBasedAssetRepository.class);

    private static final String DIR_BLOB_BASE = "blobs";
    private static final String DIR_META_BASE = "meta";

//    private File baseDirectory;

    private BlobRepositoryImpl blobRepository;

//    private ObjectMapper objectMapper = new ObjectMapper();

    public BlobBasedAssetRepository(File baseDirectory) {
        Objects.requireNonNull(baseDirectory, "baseDirectory must not be null!");

        if (!baseDirectory.exists()) {
            if (logger.isDebugEnabled())
                logger.debug("baseDirectory '{}' doesn't exist, creating it...", baseDirectory.getAbsolutePath());

            if (!baseDirectory.mkdirs()) {
                throw new IllegalArgumentException("Unable to create base directory '" + baseDirectory.getAbsolutePath() + "'!");
            }
        }

        setBaseDirectory(baseDirectory);
        if (logger.isDebugEnabled()) logger.debug("Using {} as base directory", baseDirectory.getAbsolutePath());

        File blobBaseDirectory = new File(baseDirectory, DIR_BLOB_BASE);
        if (!blobBaseDirectory.exists() && !blobBaseDirectory.mkdirs()) {
            throw new IllegalStateException("Unable to create missing BLOB base directory '" + blobBaseDirectory.getAbsolutePath() + "'!");
        }

        blobRepository = new BlobRepositoryImpl();
        blobRepository.setBaseDirectory(blobBaseDirectory);
    }

    @Override
    public Asset retrieveAsset(String base, String assetId) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving asset for base {} and id {}", base, assetId);

        AssetProperties props = read(AssetProperties.class, assetId, base);

        return AssetFactory.createFromProperties(props);
    }

    @Override
    public Map<String, Asset> retrieveAssets(String base, Collection<String> assetIds) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving assets for base {} and ids {}", base, assetIds);

        Map<String, Asset> assets = new HashMap<>();

        for (String assetId : assetIds) {
            AssetProperties props = read(AssetProperties.class, assetId, base);
            Asset asset = AssetFactory.createFromProperties(props);

            if (asset != null) {
                assets.put(assetId, asset);
            }
        }

        return assets;
    }

    @Override
    public InputStream retrieveAssetData(String assetId) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving asset data for id {}", assetId);

        try {
            return blobRepository.get(assetId);
        } catch (Throwable ex) {
            throw new IllegalArgumentException("Error reading asset data!", ex);
        }
    }

    @Override
    public long sizeOfAssetData(String assetId) {
        try {
            return blobRepository.sizeOf(assetId);
        } catch (AmbiguousIdException ex) {
            throw new IllegalArgumentException("Error reading asset data!", ex);
        }
    }

    @Override
    public <T extends Asset> T searchOrCreateAsset(Class<T> clazz, String base, InputStream inputStream) {
        if (logger.isDebugEnabled()) logger.debug("Search or create an asset of class {} for base {} ", clazz, base);

        String blobId;

        try (InputStream is = inputStream) {
            blobId = blobRepository.put(is);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error writing data to the blob repository!", ex);
        }

        Asset existingAsset = retrieveAsset(base, blobId);
        if (existingAsset == null) {
            Asset asset = AssetFactory.createEmpty(clazz);
            asset.setId(blobId);
            return (T) asset;
        }

        Class<? extends Asset> existingClass = existingAsset.getClass();
        if (!existingClass.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("An incompatible asset exists for id '" + blobId + "' and is of type '" + existingClass.getName() + "'!");
        }

        return (T) existingAsset;
    }

    @Override
    public String createAsset(String base, Asset asset, InputStream inputStream) {
        if (logger.isDebugEnabled()) logger.debug("Creating asset {} for base {} ", asset, base);

        String blobId;

        try (InputStream is = inputStream) {
            blobId = blobRepository.put(is);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error writing data to the blob repository!", ex);
        }

        asset.setId(blobId);

        AssetProperties props = AssetFactory.convertToProperties(asset);
        write(props, blobId, base);

        return blobId;
    }

    @Override
    public void updateAsset(String base, Asset asset) {
        if (logger.isDebugEnabled()) logger.debug("Updating asset {} for base {} ", asset, base);

        AssetProperties props = AssetFactory.convertToProperties(asset);
        write(props, asset.getId(), base);
    }

    @Override
    public void updateAssets(String base, Collection<Asset> assets) {
        if (logger.isDebugEnabled()) logger.debug("Updating assets {} for base {} ", assets, base);

        for (Asset asset : assets) {
            updateAsset(base, asset);
        }
    }

    @Override
    public void removeAsset(String base, String assetId) {
        if (logger.isDebugEnabled()) logger.debug("Removing asset for base {} and id {}", base, assetId);

        File propsFile = getFile(AssetProperties.class, assetId, base);
        if (propsFile.exists() && !propsFile.delete()) {
            throw new IllegalStateException("Unable to delete asset properties file '" + propsFile.getAbsolutePath() + "'!");
        }
    }

    @Override
    public void removeAssets(String base, Collection<String> assetIds) {
        if (logger.isDebugEnabled()) logger.debug("Removing assets for base {} and ids {}", base, assetIds);

        for (String assetId : assetIds) {
            removeAsset(base, assetId);
        }
    }

    @Override
    protected <T> File getFile(Class<T> target, String id, String mimeType, String... path) {
        String assetBase = DIR_META_BASE + "/" + JsonFileBasedRepository.resolveBasePath(id);
        File assetDirectory = new File(getBaseDirectory(), assetBase);

        if (!assetDirectory.exists() && !assetDirectory.mkdirs()) {
            throw new IllegalStateException("Unable to create missing asset directory '" + assetDirectory.getAbsolutePath() + "'!");
        }

        String propsFile = "props_" + path[0] + ".json";
        return new File(assetDirectory, propsFile);
    }
    
//    /* private -> testing */
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
//
//    /* private -> testing */
//    AssetProperties readAssetProperties(String base, String assetId) {
//        File propsFile = getPropsFile(base, assetId);
//        if (!propsFile.exists()) {
//            return null;
//        }
//
//        ReadWriteLock lock = getLock(base + "/" + assetId);
//        lock.readLock().lock();
//
//        try (InputStream is = new BufferedInputStream(new FileInputStream(propsFile))) {
//            return objectMapper.readValue(is, AssetProperties.class);
//        } catch (IOException ex) {
//            // TODO: create proper exception handling
//            throw new IllegalArgumentException("Error reading asset properties from file (" + propsFile.getAbsolutePath() + ")!", ex);
//        } finally {
//            lock.readLock().unlock();
//        }
//    }
//
//    /* private -> testing */
//    void writeAssetProperties(String base, String assetId, AssetProperties properties) {
//        File propsFile = getPropsFile(base, assetId);
//
//        ReadWriteLock lock = getLock(base + "/" + assetId);
//        lock.writeLock().lock();
//
//        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(propsFile))) {
//            objectMapper.writerWithDefaultPrettyPrinter().writeValue(os, properties);
//        } catch (IOException ex) {
//            // TODO: create proper exception handling
//            throw new IllegalArgumentException("Error writing asset properties to file (" + propsFile.getAbsolutePath() + ")!", ex);
//        }
//        finally {
//            lock.writeLock().unlock();
//        }
//    }
}

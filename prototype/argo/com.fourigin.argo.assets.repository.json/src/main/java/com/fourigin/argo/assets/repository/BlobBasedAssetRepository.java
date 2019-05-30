package com.fourigin.argo.assets.repository;

import com.fourigin.argo.assets.models.Asset;
import com.fourigin.argo.assets.models.AssetFactory;
import com.fourigin.argo.assets.models.AssetProperties;
import com.fourigin.argo.assets.models.AssetSearchFilter;
import com.fourigin.argo.assets.models.ImageAsset;
import com.fourigin.argo.assets.models.Index;
import com.fourigin.argo.assets.models.IndexEntry;
import com.fourigin.utilities.core.JsonFileBasedRepository;
import de.huxhorn.sulky.blobs.AmbiguousIdException;
import de.huxhorn.sulky.blobs.impl.BlobRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BlobBasedAssetRepository extends JsonFileBasedRepository implements AssetRepository, AssetIndexRepository {
    private final Logger logger = LoggerFactory.getLogger(BlobBasedAssetRepository.class);

    private static final String DIR_BLOB_BASE = "blobs";
    private static final String DIR_META_BASE = "meta";
    private static final String DIR_INDEX_BASE = "index";

    private BlobRepositoryImpl blobRepository;

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
    public Asset retrieveAsset(String language, String assetId) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving asset for language {} and id {}", language, assetId);

        AssetProperties props = read(AssetProperties.class, assetId, language);

        return fixAssets(language, AssetFactory.createFromProperties(props));
    }

    @Override
    public Map<String, Asset> retrieveAssets(String language, Collection<String> assetIds) {
        if (logger.isDebugEnabled()) logger.debug("Retrieving assets for language {} and ids {}", language, assetIds);

        Map<String, Asset> assets = new HashMap<>();

        for (String assetId : assetIds) {
            AssetProperties props = read(AssetProperties.class, assetId, language);
            Asset asset = AssetFactory.createFromProperties(props);

            if (asset != null) {
                assets.put(assetId, fixAssets(language, asset));
            }
        }

        return assets;
    }

    private Asset fixAssets(String language, Asset asset) {
        if (asset == null) {
            return null;
        }

        if (ImageAsset.class.isAssignableFrom(asset.getClass())) {
            String assetId = asset.getId();

            try {
                if (logger.isDebugEnabled()) logger.debug("Resolving image size of {}", assetId);
                ImageAsset imageAsset = (ImageAsset) asset;
                Dimension size = imageAsset.getSize();
                if (size == null) {
                    // fix missing size
                    InputStream data = retrieveAssetData(assetId);
                    BufferedImage image = ImageIO.read(data);
                    imageAsset.setSize(image.getWidth(), image.getHeight());
                    updateAsset(language, asset);
                }
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) logger.error("Unable to resolve image size of asset {}!", assetId, ex);
            }
        }

        return asset;
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
    public Map<String, Asset> findAssets(String language, AssetSearchFilter filter) {
        Index index = getIndex();
        if (index == null) {
            index = createIndex(language);
        }

        Map<String, IndexEntry> indexEntries = index.getEntries();

        Set<String> matchingIds = null;

        // width
        {
            Integer width = filter.getWidth();
            if (width != null) {
                IndexEntry entry = indexEntries.get("width");
                if (entry == null) {
                    if (logger.isInfoEnabled()) logger.info("No 'width' index found!");
                    return Collections.emptyMap();
                }

                matchingIds = applyIds(entry, String.valueOf(width.intValue()), null);
            }
        }

        // height
        {
            Integer height = filter.getHeight();
            if (height != null) {
                IndexEntry entry = indexEntries.get("height");
                if (entry == null) {
                    if (logger.isInfoEnabled()) logger.info("No 'height' index found!");
                    return Collections.emptyMap();
                }

                matchingIds = applyIds(entry, String.valueOf(height.intValue()), matchingIds);
            }
        }

        // mimeType
        {
            String mimeType = filter.getMimeType();
            if (mimeType != null) {
                IndexEntry entry = indexEntries.get("mimeType");
                if (entry == null) {
                    if (logger.isInfoEnabled()) logger.info("No 'mimeType' index found!");
                    return Collections.emptyMap();
                }

                matchingIds = applyIds(entry, mimeType, matchingIds);
            }
        }

        // tags
        {
            Set<String> tags = filter.getTags();
            if (tags != null) {
                IndexEntry entry = indexEntries.get("tags");
                if (entry == null) {
                    if (logger.isInfoEnabled()) logger.info("No 'tags' index found!");
                    return Collections.emptyMap();
                }

                for (String tag : tags) {
                    matchingIds = applyIds(entry, tag, matchingIds);
                }
            }
        }

        // keywords
        {
            String keyword = filter.getKeyword();
            if (keyword != null) {
                IndexEntry entry = indexEntries.get("keywords_" + language);
                if (entry == null) {
                    if (logger.isInfoEnabled()) logger.info("No 'keywords' index found for language '{}'!", language);
                    return Collections.emptyMap();
                }

                // TODO: implement contains-search
                matchingIds = applyIds(entry, keyword, matchingIds);
            }
        }

        if (matchingIds == null) {
            return Collections.emptyMap();
        }

        return retrieveAssets(language, matchingIds);
    }

    private Set<String> applyIds(IndexEntry entry, String value, Set<String> matchingIds) {
        Map<String, Set<String>> indexValues = entry.getValues();
        Set<String> ids = indexValues.get(value);
        if (ids == null) {
            return Collections.emptySet();
        }

        if (matchingIds == null) {
            matchingIds = new HashSet<>(ids);
        } else {
            matchingIds.retainAll(ids);
        }

        return matchingIds;
    }

    private Index createIndex(String language) {
        if (logger.isInfoEnabled()) logger.info("Building an asset index ...");
        long startTime = System.currentTimeMillis();

        Map<String, IndexEntry> entries = new HashMap<>();

        Set<String> allIds = blobRepository.idSet();
        for (String id : allIds) {
            ImageAsset asset = (ImageAsset) retrieveAsset(language, id); // TODO: cast!
            updateIndexEntries(language, asset, entries);
        }

        Index index = new Index();
        index.setEntries(entries);

        long endTime = System.currentTimeMillis();

        if (logger.isInfoEnabled()) logger.info("Asset index built in {} ms", endTime - startTime);

        updateIndex(index);

        return index;
    }

    private void updateIndexEntries(String language, ImageAsset asset, Map<String, IndexEntry> entries) {
        String assetId = asset.getId();

        int width = asset.getSize().width;
        updateEntry("width", String.valueOf(width), assetId, entries);

        int height = asset.getSize().height;
        updateEntry("height", String.valueOf(height), assetId, entries);

        updateEntry("mimeType", asset.getMimeType(), assetId, entries);

        Set<String> tags = asset.getTags();
        if (tags != null) {
            for (String tag : tags) {
                updateEntry("tags", tag, assetId, entries);
            }
        }

        updateEntry("keywords_" + language, asset.getName(), assetId, entries);
    }

    private void updateEntry(String key, String value, String assetId, Map<String, IndexEntry> entries) {
        IndexEntry propertyEntries = entries.get(key);
        if (propertyEntries == null) {
            propertyEntries = new IndexEntry();
            propertyEntries.setKey(key);
            entries.put(key, propertyEntries);
        }

        Map<String, Set<String>> values = propertyEntries.getValues();
        if (values == null) {
            values = new HashMap<>();
            propertyEntries.setValues(values);
        }

        Set<String> matchingIds = values.get(value);
        if (matchingIds == null) {
            matchingIds = new HashSet<>();
            values.put(value, matchingIds);
        }
        matchingIds.add(assetId);
    }

    @Override
    public Index getIndex() {
        File indexBase = getIndexBase();

        File[] indexFiles = indexBase.listFiles((dir, name) -> name.endsWith(".json"));
        if (indexFiles == null || indexFiles.length == 0) {
            if (logger.isWarnEnabled()) logger.warn("No asset index found at {}!", indexBase.getAbsolutePath());
            return null;
        }

        Map<String, IndexEntry> entries = new HashMap<>();
        for (File indexFile : indexFiles) {
            Map<String, Set<String>> values;
            try {
                values = getObjectMapper().readValue(indexFile, IndexEntryValues.class);
            } catch (IOException ex) {
                if (logger.isErrorEnabled())
                    logger.error("Error reading index file '{}'!", indexFile.getAbsolutePath(), ex);
                continue;
            }

            String filename = indexFile.getName();
            String key = filename.substring(0, filename.lastIndexOf(".json"));

            IndexEntry entry = new IndexEntry();
            entry.setKey(key);
            entry.setValues(values);

            entries.put(key, entry);
        }

        Index index = new Index();
        index.setEntries(entries);

        return index;
    }

    @Override
    public void updateIndex(Index index) {
        if (index == null) {
            return;
        }

        Map<String, IndexEntry> entries = index.getEntries();
        if (entries == null || entries.isEmpty()) {
            return;
        }

        File indexBase = getIndexBase();

        for (Map.Entry<String, IndexEntry> e : entries.entrySet()) {
            String key = e.getKey();
            IndexEntry entry = e.getValue();

            File indexFile = new File(indexBase, key + ".json");
            try {
                getObjectMapper().writeValue(indexFile, entry.getValues());
            } catch (IOException ex) {
                throw new IllegalStateException("Error writing index file '" + indexFile.getAbsolutePath() + "'!", ex);
            }
        }
    }

    @Override
    public <T extends Asset> T searchOrCreateAsset(Class<T> clazz, String language, InputStream inputStream) {
        if (logger.isDebugEnabled())
            logger.debug("Search or create an asset of class {} for language {} ", clazz, language);

        String blobId;

        try (InputStream is = inputStream) {
            blobId = blobRepository.put(is);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error writing data to the blob repository!", ex);
        }

        Asset existingAsset = retrieveAsset(language, blobId);
        if (existingAsset == null) {
            Asset asset = AssetFactory.createEmpty(clazz);
            asset.setId(blobId);
            //noinspection unchecked
            return (T) asset;
        }

        Class<? extends Asset> existingClass = existingAsset.getClass();
        if (!existingClass.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("An incompatible asset exists for id '" + blobId + "' and is of type '" + existingClass.getName() + "'!");
        }

        //noinspection unchecked
        return (T) existingAsset;
    }

    @Override
    public String createAsset(String language, Asset asset, InputStream inputStream) {
        if (logger.isDebugEnabled()) logger.debug("Creating asset {} for language {} ", asset, language);

        String blobId;

        try (InputStream is = inputStream) {
            blobId = blobRepository.put(is);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error writing data to the blob repository!", ex);
        }

        asset.setId(blobId);

        AssetProperties props = AssetFactory.convertToProperties(asset);
        write(props, blobId, language);

        return blobId;
    }

    @Override
    public void updateAsset(String language, Asset asset) {
        if (logger.isDebugEnabled()) logger.debug("Updating asset {} for language {} ", asset, language);

        AssetProperties props = AssetFactory.convertToProperties(asset);
        write(props, asset.getId(), language);
    }

    @Override
    public void updateAssets(String language, Collection<Asset> assets) {
        if (logger.isDebugEnabled()) logger.debug("Updating assets {} for language {} ", assets, language);

        for (Asset asset : assets) {
            updateAsset(language, asset);
        }
    }

    @Override
    public void removeAsset(String language, String assetId) {
        if (logger.isDebugEnabled()) logger.debug("Removing asset for language {} and id {}", language, assetId);

        File propsFile = getFile(AssetProperties.class, assetId, language);
        if (propsFile.exists() && !propsFile.delete()) {
            throw new IllegalStateException("Unable to delete asset properties file '" + propsFile.getAbsolutePath() + "'!");
        }
    }

    @Override
    public void removeAssets(String language, Collection<String> assetIds) {
        if (logger.isDebugEnabled()) logger.debug("Removing assets for language {} and ids {}", language, assetIds);

        for (String assetId : assetIds) {
            removeAsset(language, assetId);
        }
    }

    @Override
    protected <T> File getFile(Class<T> target, String id, String mimeType, String... path) {
        String assetBase = DIR_META_BASE + "/" + JsonFileBasedRepository.resolveBasePath(id);
        File assetDirectory = new File(getBaseDirectory(), assetBase);

        if (!assetDirectory.exists() && !assetDirectory.mkdirs()) {
            throw new IllegalStateException("Unable to create missing asset directory '" + assetDirectory.getAbsolutePath() + "'!");
        }

        String propsFile = "props_" + path[0].toUpperCase(Locale.US) + ".json";
        return new File(assetDirectory, propsFile);
    }

    private File getIndexBase() {
        File indexDirectory = new File(getBaseDirectory(), DIR_INDEX_BASE);

        if (!indexDirectory.exists() && !indexDirectory.mkdirs()) {
            throw new IllegalStateException("Unable to create missing index directory '" + indexDirectory.getAbsolutePath() + "'!");
        }

        return indexDirectory;
    }
}

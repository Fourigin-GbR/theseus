package com.fourigin.argo.assets.repository;

import com.fourigin.argo.assets.models.Asset;

import java.io.InputStream;
import java.util.Collection;

public interface AssetRepository extends AssetResolver {
    <T extends Asset> T searchOrCreateAsset(Class<T> clazz, String locale, InputStream inputStream);
    String createAsset(String locale, Asset asset, InputStream inputStream);

    void updateAsset(String locale, Asset asset);
    void updateAssets(String locale, Collection<Asset> assets);

    void removeAsset(String locale, String assetId);
    void removeAssets(String locale, Collection<String> assetIds);
}

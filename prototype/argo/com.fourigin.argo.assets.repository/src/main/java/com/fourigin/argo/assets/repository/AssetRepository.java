package com.fourigin.argo.assets.repository;

import com.fourigin.argo.assets.models.Asset;

import java.io.InputStream;
import java.util.Collection;

public interface AssetRepository extends AssetResolver {
    <T extends Asset> T searchOrCreateAsset(Class<T> clazz, String base, InputStream inputStream);
    String createAsset(String base, Asset asset, InputStream inputStream);

    void updateAsset(String base, Asset asset);
    void updateAssets(String base, Collection<Asset> assets);

    void removeAsset(String base, String assetId);
    void removeAssets(String base, Collection<String> assetIds);
}

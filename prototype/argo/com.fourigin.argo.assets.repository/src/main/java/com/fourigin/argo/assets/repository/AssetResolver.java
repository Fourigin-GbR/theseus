package com.fourigin.argo.assets.repository;

import com.fourigin.argo.assets.models.Asset;
import com.fourigin.argo.assets.models.AssetSearchFilter;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public interface AssetResolver {
    Asset retrieveAsset(String language, String assetId);

    Map<String, Asset> retrieveAssets(String language, Collection<String> assetIds);

    InputStream retrieveAssetData(String assetId);

    long sizeOfAssetData(String assetId);

    Map<String, Asset> findAssets(String language, AssetSearchFilter filter);
}

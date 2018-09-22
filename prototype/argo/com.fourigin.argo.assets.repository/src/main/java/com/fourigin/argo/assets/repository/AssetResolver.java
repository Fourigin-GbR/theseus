package com.fourigin.argo.assets.repository;

import com.fourigin.argo.assets.models.Asset;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public interface AssetResolver {
    Asset retrieveAsset(String base, String assetId);

    Map<String, Asset> retrieveAssets(String base, Collection<String> assetIds);

    InputStream retrieveAssetData(String assetId);

    long sizeOfAssetData(String assetId);
}

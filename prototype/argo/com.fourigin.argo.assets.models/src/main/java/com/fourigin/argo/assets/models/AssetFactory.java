package com.fourigin.argo.assets.models;

import java.util.Map;
import java.util.Set;

public final class AssetFactory {
    public static final String PROP_ASSET_CLASS_NAME = "class-name";
    public static final String PROP_ASSET_ID = "id";
    public static final String PROP_ASSET_NAME = "name";
    public static final String PROP_ASSET_MIME_TYPE = "mime-type";
    public static final String PROP_ASSET_TAGS = "tags";
    public static final String PROP_ASSET_ATTRIBUTES = "attributes";
    public static final String PROP_ASSET_SIZE = "size";

    private AssetFactory() {
    }

    public static <T extends Asset> T createEmpty(Class<T> clazz) {
        if (ImageAsset.class.isAssignableFrom(clazz)) {
            return (T) new ImageAsset();
        }

        throw new IllegalArgumentException("Unsupported asset type '" + clazz.getName() + "'!");
    }

    public static <T extends Asset> T createFromProperties(AssetProperties assetProperties) {
        if (assetProperties == null) {
            return null;
        }

        String className = (String) assetProperties.get(PROP_ASSET_CLASS_NAME);

        if (ImageAsset.class.getName().equals(className)) {
            // read properties
            String id = (String) assetProperties.get(PROP_ASSET_ID);

            String name = (String) assetProperties.get(PROP_ASSET_NAME);

            String mimeType = (String) assetProperties.get(PROP_ASSET_MIME_TYPE);

            //noinspection unchecked
            Set<String> tags = (Set<String>) assetProperties.get(PROP_ASSET_TAGS);

            //noinspection unchecked
            Map<String, String> attributes = (Map<String, String>) assetProperties.get(PROP_ASSET_ATTRIBUTES);

            // instantiate the object
            ImageAsset asset = new ImageAsset();
            asset.setId(id);
            asset.setName(name);
            asset.setMimeType(mimeType);
            if (tags != null) {
                asset.setTags(tags);
            }
            if (attributes != null && !attributes.isEmpty()) {
                asset.setAttributes(attributes);
            }

            String size = (String) assetProperties.get(PROP_ASSET_SIZE);
            if (size != null) {
                int pos = size.indexOf('x');
                String widthStr = size.substring(0, pos);
                String heightStr = size.substring(pos + 1);

                asset.setSize(Integer.parseInt(widthStr), Integer.parseInt(heightStr));
            }

            return (T) asset;
        }

        throw new IllegalArgumentException("Unsupported asset type '" + className + "'!");
    }

    public static <T extends Asset> AssetProperties convertToProperties(T asset) {
        AssetProperties assetProperties = new AssetProperties();

        assetProperties.put(PROP_ASSET_CLASS_NAME, asset.getClass().getName());
        assetProperties.put(PROP_ASSET_ID, asset.getId());
        assetProperties.put(PROP_ASSET_NAME, asset.getName());
        assetProperties.put(PROP_ASSET_MIME_TYPE, asset.getMimeType());

        Set<String> tags = asset.getTags();
        if (tags != null && !tags.isEmpty()) {
            assetProperties.put(PROP_ASSET_TAGS, tags);
        }

        Map<String, String> attributes = asset.getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            assetProperties.put(PROP_ASSET_ATTRIBUTES, attributes);
        }

        if (ImageAsset.class.isAssignableFrom(asset.getClass())) {
            ImageAsset imageAsset = (ImageAsset) asset;
            assetProperties.put(PROP_ASSET_SIZE, imageAsset.getSize().width + "x" + imageAsset.getSize().height);
        }

        return assetProperties;
    }
}

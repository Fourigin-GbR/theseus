package com.fourigin.argo.controller.assets;

import com.fourigin.argo.assets.models.Asset;
import com.fourigin.argo.assets.repository.AssetResolver;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Set;


public class ThumbnailResolver {
    private final Logger logger = LoggerFactory.getLogger(ThumbnailResolver.class);

    private static final String DEFAULT_NAME = "DEFAULT";
    private static final String EXTENSION = ".png";

    private AssetResolver assetResolver;

    private String targetDirectory;
    private String unsupportedMimeTypesIconDirectory;

    private Thumbnailer thumbnailer = new Thumbnailer();

    public InputStream resolveThumbnail(Asset asset, Dimension desiredSize, Set<String> allowedMimeTypes) {

        String blobId = asset.getId();
        String mimeType = asset.getMimeType();

        // TODO: Check mime type. Only generate thumbs for images. Use predefined thumbs for known and unknown mimes.
        if (!allowedMimeTypes.contains(mimeType)) {
            if (logger.isErrorEnabled())
                logger.error("Unsuitable MIME type '{}'! Valid values are {}.", mimeType, allowedMimeTypes);

            int i = mimeType.indexOf("/");
            String mimeSuffix = mimeType.substring(i + 1).toUpperCase(Locale.US);
            String iconName = mimeSuffix + EXTENSION;

            File iconFile = new File(unsupportedMimeTypesIconDirectory, iconName);
            if (!iconFile.exists()) {
                if (logger.isErrorEnabled()) logger.error("Unknown MIME type '{}'", mimeType);
                iconName = DEFAULT_NAME + EXTENSION;
                iconFile = new File(unsupportedMimeTypesIconDirectory, iconName);
            }

            try {
                return FileUtils.openInputStream(iconFile);
            } catch (IOException e) {
                if (logger.isWarnEnabled())
                    logger.warn("Couldn't open stream for '" + iconFile.getAbsolutePath() + "'!", e);
            }

            return null;
        }

        File directoryFile = new File(targetDirectory);
        initDirectory(directoryFile);

        File sizeBaseDirectory = new File(targetDirectory, desiredSize.getWidth() + "x" + desiredSize.getHeight());
        initDirectory(sizeBaseDirectory);

        File resizedFile = new File(sizeBaseDirectory, blobId);

        if (resizedFile.isFile() && resizedFile.length() <= 0) {
            if (resizedFile.delete()) {
                if (logger.isDebugEnabled()) logger.debug("Deleted empty file '{}'.", resizedFile.getAbsolutePath());
            } else {
                if (logger.isWarnEnabled())
                    logger.warn("Couldn't delete empty file '{}'!", resizedFile.getAbsolutePath());
            }
        }

        try {
            if (resizedFile.createNewFile()) {
                BufferedImage originImage = thumbnailer.readImage(assetResolver.retrieveAssetData(blobId));
                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(resizedFile))) {
                    thumbnailer.createThumbnail(originImage, os, desiredSize);
                    if (logger.isDebugEnabled())
                        logger.debug("Created new thumbnail at {}.", resizedFile.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            if (logger.isWarnEnabled()) logger.warn("Exception while creating thumbnail!", e);
        }

        if (resizedFile.length() <= 0) {
            if (logger.isWarnEnabled())
                logger.warn("Thumbnail '{}' is not a valid file.", resizedFile.getAbsolutePath());
            resizedFile = new File(unsupportedMimeTypesIconDirectory, DEFAULT_NAME + EXTENSION);
        }

        InputStream result = null;
        try {
            result = FileUtils.openInputStream(resizedFile);
        } catch (IOException e) {
            if (logger.isWarnEnabled())
                logger.warn("Couldn't open stream for '" + resizedFile.getAbsolutePath() + "'!", e);
        }

        return result;
    }

    private void initDirectory(File dir){
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                if (logger.isDebugEnabled())
                    logger.debug("Created a directory '{}'.", dir.getAbsolutePath());
            } else {
                if (logger.isWarnEnabled())
                    logger.warn("Couldn't create directory '{}'!", dir.getAbsolutePath());
            }
        }

        if (!dir.isDirectory()) {
            throw new IllegalStateException("'" + dir.getAbsolutePath() + "' is not a directory!");
        }

    }

    public void setAssetResolver(AssetResolver assetResolver) {
        this.assetResolver = assetResolver;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public void setUnsupportedMimeTypesIconDirectory(String unsupportedMimeTypesIconDirectory) {
        this.unsupportedMimeTypesIconDirectory = unsupportedMimeTypesIconDirectory;
    }
}

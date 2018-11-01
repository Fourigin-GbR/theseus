package com.fourigin.argo.assets.models;

import com.fourigin.utilities.core.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class Assets {
    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter
        .ofPattern("yyyyMMdd'T'HHmmssSSSX", Locale.US)
        .withZone(ZoneOffset.UTC);

    private static final char[] VALID_CHAR_ARRAY = new char[]{
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '-', '_', '.',
    };

    private static final Set<Character> VALID_CHARS = new HashSet<>();

    static {
        for (char c : VALID_CHAR_ARRAY) {
            VALID_CHARS.add(c);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Assets.class);

    private Assets() {
    }

    public static String resolveSanitizedBasename(String original) {
        if (original == null) {
            return null;
        }

        final Logger logger = LoggerFactory.getLogger(Assets.class);

        String mime = MimeTypes.resolveMimeType(original);

        String result = original.toLowerCase(Locale.US);
        if (result.endsWith(".jpeg")) {
            result = result.replace(".jpeg", ".jpg");
        }
        result = result.replace("%20", "_");

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < result.length(); i++) {
            char current = result.charAt(i);

            if (VALID_CHARS.contains(current)) {
                b.append(current);
            } else {
                b.append("_");
            }
        }
        result = b.toString();

        while (result.contains("__")) {
            result = result.replace("__", "_");
        }

        if (mime != null) {
            // remove extension
            int lastDot = result.lastIndexOf('.');
            if (lastDot > -1) {
                result = result.substring(0, lastDot);
            }
        }

        if (logger.isDebugEnabled()) logger.debug("Resolved {} to {}.", original, result);

        return result;
    }

    public static Map<String, String> sanitizeLocalizedNames(Map<String, String> resourceAttributes) {
        if (resourceAttributes != null) {
            for (Map.Entry<String, String> current : resourceAttributes.entrySet()) {
                String key = current.getKey();
                if (key.startsWith(Asset.LOCALIZED_FILENAME_PREFIX)) {
                    String value = current.getValue();
                    value = resolveSanitizedBasename(value);
                    current.setValue(value);
                }
            }
        }
        return resourceAttributes; // for convenience
    }

    public static String resolveLocalizedFileName(Asset asset, String localeCode) {
        final Logger logger = LoggerFactory.getLogger(Assets.class);
        Objects.requireNonNull(asset, "resource must not be null!");
        Objects.requireNonNull(localeCode, "localeCode must not be null!");

        String fileName = asset.getAttribute(Asset.LOCALIZED_FILENAME_PREFIX + localeCode);
        if (fileName == null) {
            if (logger.isDebugEnabled()) logger.debug("No localized filename found.");
            fileName = asset.getName();
        }
        if (fileName == null) {
            return null;
        }
        String mime = asset.getMimeType();
        if (mime != null) {
            String extension = MimeTypes.resolveFileExtension(mime);
            if (extension != null) {
                fileName = fileName + "." + extension;
            }
        }
        return fileName;
    }

    public static String resolveLocalizedAltText(Asset resource, String localeCode) {
        final Logger logger = LoggerFactory.getLogger(Assets.class);
        Objects.requireNonNull(resource, "resource must not be null!");
        Objects.requireNonNull(localeCode, "localeCode must not be null!");

        String altText = resource.getAttribute(Asset.LOCALIZED_ALT_TEXT_PREFIX + localeCode);
        if (altText == null) {
            if (logger.isDebugEnabled()) logger.debug("No localized alt text found.");
            altText = resource.getAttribute(Asset.DEFAULT_ALT_TEXT);
        }
        return altText;
    }

    public static String getISO8601Date(long timestamp) {
        return ISO_DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(timestamp));
    }

    public static Map<String, String> createdBy(String username, long timestamp, Map<String, String> attributes) {
        Objects.requireNonNull(username, "username must not be null!");
        if (attributes == null) {
            attributes = new HashMap<>();
        }

        attributes.put(Asset.CREATED_BY_ATTRIBUTE_NAME, username);
        attributes.put(Asset.CREATION_DATE_ATTRIBUTE_NAME, getISO8601Date(timestamp));
        attributes.put(Asset.CREATION_TIMESTAMP_ATTRIBUTE_NAME, "" + timestamp);

        return changedBy(username, timestamp, attributes);
    }

    public static Map<String, String> changedBy(String username, long timestamp, Map<String, String> attributes) {
        Objects.requireNonNull(username, "username must not be null!");
        if (attributes == null) {
            attributes = new HashMap<>();
        }

        attributes.put(Asset.CHANGED_BY_ATTRIBUTE_NAME, username);
        attributes.put(Asset.CHANGE_DATE_ATTRIBUTE_NAME, getISO8601Date(timestamp));
        attributes.put(Asset.CHANGE_TIMESTAMP_ATTRIBUTE_NAME, "" + timestamp);

        return attributes;
    }

    public static String resolveAssetBasePath(String assetId){
        String firstBlobPart = assetId.substring(0,2);
        String remainingBlobPart = assetId.substring(2);
        return firstBlobPart + "/" + remainingBlobPart;
    }

    public static String resolveAssetFileName(String base, Asset asset){
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Resolving localized filename for base {} and {}", base, asset);

        String mimeType = asset.getMimeType();
        String fileEnding = MimeTypes.resolveFileExtension(mimeType);

        // TODO: use base to resolve localized filename

        return asset.getName() + "." + fileEnding;
    }
}

package com.fourigin.utilities.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class MimeTypes {
    private static final Map<String, String> EXTENSION_MIME_TYPE_MAPPING = new HashMap<>();
    private static final Map<String, String> MIME_TYPE_EXTENSION_MAPPING = new HashMap<>();

    private MimeTypes(){
    }

    static {
        EXTENSION_MIME_TYPE_MAPPING.put("avi", "video/avi");
        EXTENSION_MIME_TYPE_MAPPING.put("bmp", "image/bmp");
        EXTENSION_MIME_TYPE_MAPPING.put("css", "text/css");
        EXTENSION_MIME_TYPE_MAPPING.put("gif", "image/gif");
        EXTENSION_MIME_TYPE_MAPPING.put("flv", "video/x-flv");
        EXTENSION_MIME_TYPE_MAPPING.put("f4v", "video/x-flv");
        EXTENSION_MIME_TYPE_MAPPING.put("htm", "text/html");
        EXTENSION_MIME_TYPE_MAPPING.put("html", "text/html");
        EXTENSION_MIME_TYPE_MAPPING.put("ico", "image/vnd.microsoft.icon");
        EXTENSION_MIME_TYPE_MAPPING.put("jpeg", "image/jpeg");
        EXTENSION_MIME_TYPE_MAPPING.put("jpg", "image/jpeg");
        EXTENSION_MIME_TYPE_MAPPING.put("js", "text/javascript");
        EXTENSION_MIME_TYPE_MAPPING.put("json", "application/json");
        EXTENSION_MIME_TYPE_MAPPING.put("mov", "video/quicktime");
        EXTENSION_MIME_TYPE_MAPPING.put("qt", "video/quicktime");
        EXTENSION_MIME_TYPE_MAPPING.put("mp3", "audio/mpeg3");
        EXTENSION_MIME_TYPE_MAPPING.put("pdf", "application/pdf");
        EXTENSION_MIME_TYPE_MAPPING.put("php", "application/x-php");
        EXTENSION_MIME_TYPE_MAPPING.put("php3", "application/x-php");
        EXTENSION_MIME_TYPE_MAPPING.put("php4", "application/x-php");
        EXTENSION_MIME_TYPE_MAPPING.put("php5", "application/x-php");
        EXTENSION_MIME_TYPE_MAPPING.put("png", "image/png");
        EXTENSION_MIME_TYPE_MAPPING.put("swf", "application/x-shockwave-flash");
        EXTENSION_MIME_TYPE_MAPPING.put("tif", "image/tiff");
        EXTENSION_MIME_TYPE_MAPPING.put("tiff", "image/tiff");
        EXTENSION_MIME_TYPE_MAPPING.put("txt", "text/plain");
        EXTENSION_MIME_TYPE_MAPPING.put("wav", "audio/wav");
        EXTENSION_MIME_TYPE_MAPPING.put("xml", "text/xml");
        EXTENSION_MIME_TYPE_MAPPING.put("zip", "application/zip");

        MIME_TYPE_EXTENSION_MAPPING.put("video/avi", "avi");
        MIME_TYPE_EXTENSION_MAPPING.put("image/bmp", "bmp");
        MIME_TYPE_EXTENSION_MAPPING.put("text/css", "css");
        MIME_TYPE_EXTENSION_MAPPING.put("image/gif", "gif");
        MIME_TYPE_EXTENSION_MAPPING.put("text/html", "html");
        MIME_TYPE_EXTENSION_MAPPING.put("image/vnd.microsoft.icon", "ico");
        MIME_TYPE_EXTENSION_MAPPING.put("image/jpeg", "jpeg");
        MIME_TYPE_EXTENSION_MAPPING.put("text/javascript", "js");
        MIME_TYPE_EXTENSION_MAPPING.put("video/quicktime", "mov");
        MIME_TYPE_EXTENSION_MAPPING.put("audio/mpeg3", "mp3");
        MIME_TYPE_EXTENSION_MAPPING.put("application/pdf", "pdf");
        MIME_TYPE_EXTENSION_MAPPING.put("application/x-php", "php");
        MIME_TYPE_EXTENSION_MAPPING.put("video/x-flv", "flv");
        MIME_TYPE_EXTENSION_MAPPING.put("image/png", "png");
        MIME_TYPE_EXTENSION_MAPPING.put("application/x-shockwave-flash", "swf");
        MIME_TYPE_EXTENSION_MAPPING.put("image/tiff", "tiff");
        MIME_TYPE_EXTENSION_MAPPING.put("application/json", "json");
        MIME_TYPE_EXTENSION_MAPPING.put("text/plain", "txt");
        MIME_TYPE_EXTENSION_MAPPING.put("audio/wav", "wav");
        MIME_TYPE_EXTENSION_MAPPING.put("text/xml", "xml");
        MIME_TYPE_EXTENSION_MAPPING.put("application/zip", "zip");

        // sanity check
        for (Map.Entry<String, String> current : EXTENSION_MIME_TYPE_MAPPING.entrySet()) {
            assert (MIME_TYPE_EXTENSION_MAPPING.containsKey(current.getValue()));
        }

        for (Map.Entry<String, String> current : MIME_TYPE_EXTENSION_MAPPING.entrySet()) {
            assert (EXTENSION_MIME_TYPE_MAPPING.containsKey(current.getValue()));
        }
    }

    public static String resolveMimeType(String fileName) {
        if (fileName == null) {
            return null;
        }

        final Logger logger = LoggerFactory.getLogger(MimeTypes.class);

        fileName = fileName.toLowerCase(Locale.US);
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot <= 0) {
            return null;
        }

        String extension = fileName.substring(lastDot + 1);
        if (logger.isDebugEnabled()) logger.debug("extension: {}", extension);

        String result = EXTENSION_MIME_TYPE_MAPPING.get(extension);
        if (logger.isDebugEnabled()) logger.debug("MimeType for extension {}: {}", extension, result);

        return result;
    }

    public static String resolveFileExtension(String mimeType) {
        if (mimeType == null) {
            return null;
        }

        final Logger logger = LoggerFactory.getLogger(MimeTypes.class);

        String result = MIME_TYPE_EXTENSION_MAPPING.get(mimeType);
        if (logger.isDebugEnabled()) logger.debug("Extension for MimeType {}: {}", mimeType, result);

        return result;
    }
}

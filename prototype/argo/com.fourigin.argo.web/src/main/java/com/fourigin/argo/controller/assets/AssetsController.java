package com.fourigin.argo.controller.assets;

import com.fourigin.argo.assets.models.Asset;
import com.fourigin.argo.assets.models.Assets;
import com.fourigin.argo.assets.models.ImageAsset;
import com.fourigin.argo.assets.repository.AssetRepository;
import com.fourigin.argo.controller.compile.RequestParameters;
import com.fourigin.utilities.core.MimeTypes;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/{customer}/assets")
public class AssetsController {

    private final Logger logger = LoggerFactory.getLogger(AssetsController.class);

    private AssetRepository assetRepository;

    private ThumbnailResolver thumbnailResolver;

    private ThumbnailDimensions dimensions;

    private static final Set<String> ALLOWED_MIME_TYPES;

    static {
        ALLOWED_MIME_TYPES = new HashSet<>();
        ALLOWED_MIME_TYPES.add("image/gif");
        ALLOWED_MIME_TYPES.add("image/jpeg");
        ALLOWED_MIME_TYPES.add("image/png");
    }

    public AssetsController(
        AssetRepository assetRepository,
        ThumbnailResolver thumbnailResolver,
        ThumbnailDimensions dimensions
    ) {
        this.assetRepository = assetRepository;
        this.thumbnailResolver = thumbnailResolver;
        this.dimensions = dimensions;
    }

    @RequestMapping("/info")
    public Asset resolveAsset(
        @PathVariable String customer,
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam("id") String assetId
    ) {
        if (logger.isDebugEnabled()) logger.debug("Processing resolveAsset request for base {} and id {}", base, assetId);

        MDC.put("customer", customer);
        MDC.put("base", base);

        try {
            Asset asset = assetRepository.retrieveAsset(base, assetId);

            if (logger.isDebugEnabled()) logger.debug("Returning asset {} for id {}", asset, assetId);

            return asset;
        }
        finally {
            MDC.remove("customer");
            MDC.remove("base");
        }
    }

    @RequestMapping("/data")
    public void resolveAsset(
        @PathVariable String customer,
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam("id") String assetId,
        HttpServletResponse response
    ) {
        if (logger.isDebugEnabled()) logger.debug("Processing resolveAssetData request for base {} and id {}", base, assetId);

        MDC.put("customer", customer);
        MDC.put("base", base);

        Asset asset = assetRepository.retrieveAsset(base, assetId);

        response.setContentType(asset.getMimeType());

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            InputStream is = assetRepository.retrieveAssetData(assetId);

            org.apache.commons.io.IOUtils.copyLarge(is, outputStream);
        }
        catch (Throwable th){
            if (logger.isErrorEnabled()) logger.error("Unexpected error occurred!", th);
        }
        finally {
            MDC.remove("customer");
            MDC.remove("base");
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, headers = ("content-type=multipart/*"), consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadAssetsResponse upload(
        @PathVariable String customer,
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam("file") MultipartFile multipartFile
    ) {
        Objects.requireNonNull(base, "base must not be null!");

        MDC.put("customer", customer);
        MDC.put("base", base);

        try {
            UploadAssetsResponse result = new UploadAssetsResponse();

            String originalFileName = multipartFile.getOriginalFilename();

            String contentType = multipartFile.getContentType();

            File tmp = null;
            try (InputStream is = multipartFile.getInputStream()) {
                tmp = File.createTempFile("upload", "");
                try (OutputStream os = new FileOutputStream(tmp)) {
                    IOUtils.copyLarge(is, os);
                }
            } catch (IOException ex) {
                if (logger.isWarnEnabled()) logger.warn("Error copying file", ex);
                if (tmp != null && !tmp.delete()) {
                    if (logger.isWarnEnabled()) // NOPMD
                        logger.warn("Unable to remove temp file '{}'", tmp.getAbsolutePath()); // NOPMD
                }
                tmp = null;
                result.registerFail(originalFileName, ex);
            }

            if (tmp != null) {
                try (InputStream is = new BufferedInputStream(new FileInputStream(tmp))) {
                    Asset asset = readAsset(base, originalFileName, multipartFile.getSize(), contentType, is);

                    result.registerSuccess(asset);
                } catch (IOException | IllegalArgumentException ex) {
                    if (logger.isErrorEnabled())
                        logger.error("Unable to read asset {}!", originalFileName, ex);
                    result.registerFail(originalFileName, ex);
                }
            }

            return result;
        }
        finally {
            MDC.remove("customer");
            MDC.remove("base");
        }
    }

    @RequestMapping(value = "/uploadUrl")
    public UploadAssetsResponse uploadUrl(
        @PathVariable String customer,
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam("filename") String filename,
        @RequestParam("contentType") String contentType,
        @RequestParam("url") String sourceUrl
    ) {
        Objects.requireNonNull(base, "base must not be null!");

        MDC.put("customer", customer);
        MDC.put("base", base);

        try {
            UploadAssetsResponse result = new UploadAssetsResponse();

            URL url;
            try {
                url = new URL(sourceUrl);
            } catch (MalformedURLException ex) {
                if (logger.isErrorEnabled()) logger.error("Invalid source url '{}'!", sourceUrl, ex);
                result.registerFail(filename, ex);
                return result;
            }

            File tmp = null;
            try (InputStream is = url.openStream()) {
                tmp = File.createTempFile("upload", "");
                try (OutputStream os = new FileOutputStream(tmp)) {
                    IOUtils.copyLarge(is, os);
                }
            } catch (IOException ex) {
                if (logger.isWarnEnabled()) logger.warn("Error copying file", ex);
                if (tmp != null && !tmp.delete()) {
                    if (logger.isWarnEnabled()) // NOPMD
                        logger.warn("Unable to remove temp file '{}'", tmp.getAbsolutePath()); // NOPMD
                }
                tmp = null;
                result.registerFail(filename, ex);
            }

            if (tmp != null) {
                try (InputStream is = new BufferedInputStream(new FileInputStream(tmp))) {
                    Asset asset = readAsset(base, filename, tmp.length(), contentType, is);

                    result.registerSuccess(asset);
                } catch (IOException | IllegalArgumentException ex) {
                    if (logger.isErrorEnabled())
                        logger.error("Unable to read asset {}!", filename, ex);
                    result.registerFail(filename, ex);
                }
            }

            return result;
        } finally {
            MDC.remove("customer");
            MDC.remove("base");
        }
    }

    @RequestMapping(value = "/thumbnail/{dimension}", method = RequestMethod.GET)
    public void getThumbnail(
        @PathVariable String customer,
        @PathVariable("dimension") String dimensionName,
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam("id") String assetId,
        @RequestHeader(value = RequestResponseConstants.IF_NONE_MATCH_REQUEST_HEADER, required = false) String etag,
        HttpServletResponse response
    ) throws IOException {

        MDC.put("customer", customer);
        MDC.put("base", base);

        Dimension desiredDimension = dimensions.get(dimensionName);
        if (desiredDimension == null) {
            throw new IllegalArgumentException("No dimension found for name '" + dimensionName + "'! Available names are: " + dimensions.keySet());
        }

        Asset asset = assetRepository.retrieveAsset(base, assetId);
        if (asset == null) {
            throw new IllegalArgumentException("Could not find asset for id '" + assetId + "'!");
        }

        if (assetId.equals(etag)) {
            response.setHeader(RequestResponseConstants.ETAG_RESPONSE_HEADER, assetId);
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            if (logger.isDebugEnabled()) logger.debug("Unchanged data: {}, etag: {}", assetId, etag);
            return;
        }

        if (logger.isDebugEnabled()) logger.debug("Changed data: {}, etag: {}", assetId, etag);

        response.setContentType(RequestResponseConstants.CONTENT_TYPE_PNG);

        try (InputStream input = thumbnailResolver.resolveThumbnail(asset, desiredDimension, ALLOWED_MIME_TYPES)) {
            if (input == null) {
                response.setHeader(RequestResponseConstants.ETAG_RESPONSE_HEADER, ""); // reset ETag
                throw new IllegalStateException("Could not find blob for id '" + assetId + "'!");
            }
            response.setHeader(RequestResponseConstants.ETAG_RESPONSE_HEADER, assetId);
            try (ServletOutputStream output = response.getOutputStream()) {
                long count = IOUtils.copyLarge(input, output);
                if (logger.isInfoEnabled())
                    logger.info("Returned {} bytes of data for resource {}.", count, assetId);
            }
        }
        finally {
            MDC.remove("customer");
            MDC.remove("base");
        }
    }

    private Asset readAsset(String base, String originalFileName, long fileSize, String contentType, InputStream inputStream) {
        String fileName = Assets.resolveSanitizedBasename(originalFileName);
        String mimeType = MimeTypes.resolveMimeType(originalFileName);
        if (logger.isDebugEnabled())
            logger.debug("Reading asset from file '{}' (mimeType: {}, contentType: {})", originalFileName, mimeType, contentType);

        // Empty file name
        if (fileName == null || fileName.isEmpty()) {
            if (logger.isErrorEnabled()) logger.error("File name is empty");

            throw new IllegalArgumentException("Filename must not be null or empty!");
        }

        // Empty MIME type
        if (mimeType == null || mimeType.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("Couldn't resolve MIME type for '{}'.", originalFileName);

            throw new IllegalArgumentException("Mime-Type must not be null or empty!");
        }

        // Unsupported file extension
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            if (logger.isErrorEnabled()) logger.error("Unsupported MIME type for file '{}' ", originalFileName);

            throw new IllegalArgumentException("Mime-Type '" + mimeType + "' is not supported!");
        }

        // Empty file
        if (fileSize == 0) {
            if (logger.isErrorEnabled()) logger.error("File '{}' is empty", originalFileName);

            throw new IllegalArgumentException("File must not be empty!");
        }

        try {
            Asset asset = createAsset(base, originalFileName, mimeType, inputStream);

            assetRepository.updateAsset(base, asset);

            return asset;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to create asset!", ex);
        }
    }

    private Asset createAsset(String base, String fileName, String mimeType, final InputStream inputStream) throws IOException {
        ImageAsset asset = assetRepository.searchOrCreateAsset(ImageAsset.class, base, inputStream);

        String assetId = asset.getId();
        if (logger.isDebugEnabled()) logger.debug("Asset-ID: {}", assetId);

        // filename
        asset.setName(Assets.resolveSanitizedBasename(fileName));

        // mime type
        if (logger.isDebugEnabled()) logger.debug("File name: '{}'; MIME type: '{}'", fileName, mimeType);

        try (InputStream data = assetRepository.retrieveAssetData(assetId)) {
            // https://github.com/haraldk/TwelveMonkeys/issues/96
            ImageInputStream iis = ImageIO.createImageInputStream(new BufferedInputStream(data));
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader imageReader = readers.next();
                String formatName = imageReader.getFormatName();

                if (logger.isInfoEnabled())
                    logger.info("FormatName: '{}'; File name: '{}'; MIME type by extension: '{}'", formatName, fileName, mimeType);
                if (formatName != null) {
                    formatName = formatName.toLowerCase(Locale.US);
                    // the code below works for gif, jpg and png.
                    // i.e. the currently allowed formats.
                    // Additional formats may require more complex logic.
                    String mimeByReader = MimeTypes.resolveMimeType("foo." + formatName);
                    if (mimeByReader == null) {
                        if (logger.isWarnEnabled())
                            logger.warn("Failed to resolve mime type of format '{}'!", formatName);
                    } else if (!mimeByReader.equals(mimeType)) {
                        if (logger.isWarnEnabled())
                            logger.warn("Replacing extension mime type '{}' with actual image reader mime type '{}'!", mimeType, mimeByReader);
                        mimeType = mimeByReader;
                    }
                }

                imageReader.setInput(iis);
                BufferedImage bufferedImage = imageReader.read(0);
                asset.setSize(bufferedImage.getWidth(), bufferedImage.getHeight());
                bufferedImage.flush();
            }
        }

        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException("Unsupported mimeType " + mimeType + " detected");
        }

        asset.setMimeType(mimeType);

        if (logger.isDebugEnabled()) logger.debug("New asset: {}", asset);

        return asset;
    }
}

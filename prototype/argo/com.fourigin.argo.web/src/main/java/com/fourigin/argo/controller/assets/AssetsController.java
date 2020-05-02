package com.fourigin.argo.controller.assets;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.fourigin.argo.assets.models.Asset;
import com.fourigin.argo.assets.models.AssetSearchFilter;
import com.fourigin.argo.assets.models.Assets;
import com.fourigin.argo.assets.models.ImageAsset;
import com.fourigin.argo.assets.repository.AssetRepository;
import com.fourigin.argo.controller.RequestParameters;
import com.fourigin.utilities.core.MimeTypes;
import com.fourigin.utilities.image.ImageUtils;
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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/{project}/assets")
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

    public interface AssetCreatingOption {
    }

    public static class FixAssetOrientationOption implements AssetCreatingOption {
    }

    public static class ResizeAssetOption implements AssetCreatingOption {
        private int maxDimension;

        public ResizeAssetOption(int maxDimension) {
            this.maxDimension = maxDimension;
        }

        public int getMaxDimension() {
            return maxDimension;
        }
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

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Asset resolveAsset(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(RequestParameters.ASSET_ID) String assetId
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing resolveAsset request for language {} and id {}", language, assetId);

        MDC.put("project", project);
        MDC.put("language", language);

        try {
            Asset asset = assetRepository.retrieveAsset(language, assetId);

            if (logger.isDebugEnabled()) logger.debug("Returning asset {} for id {}", asset, assetId);

            return asset;
        } finally {
            MDC.remove("language");
            MDC.remove("project");
        }
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public void resolveAsset(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(RequestParameters.ASSET_ID) String assetId,
            HttpServletResponse response
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Processing resolveAssetData request for language {} and id {}", language, assetId);

        MDC.put("project", project);
        MDC.put("language", language);

        Asset asset = assetRepository.retrieveAsset(language, assetId);

        response.setContentType(asset.getMimeType());

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            InputStream is = assetRepository.retrieveAssetData(assetId);

            org.apache.commons.io.IOUtils.copyLarge(is, outputStream);
        } catch (Throwable th) {
            if (logger.isErrorEnabled()) logger.error("Unexpected error occurred!", th);
        } finally {
            MDC.remove("language");
            MDC.remove("project");
        }
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public Map<String, Asset> searchAssets(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(value = "w", required = false) Integer width,
            @RequestParam(value = "h", required = false) Integer height,
            @RequestParam(value = "m", required = false) String mimeType,
            @RequestParam(value = "t", required = false) Set<String> tags,
            @RequestParam(value = "k", required = false) String keyword
    ) {
        if (logger.isDebugEnabled()) logger.debug("Processing search assets request for language {}", language);

        AssetSearchFilter filter = new AssetSearchFilter.Builder()
                .withWidth(width)
                .withHeight(height)
                .withMimeType(mimeType)
                .withTags(tags)
                .withKeyword(keyword)
                .build();

        return assetRepository.findAssets(language, filter);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, headers = ("content-type=multipart/*"), consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadAssetsResponse upload(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(RequestParameters.ASSET_FILE) MultipartFile multipartFile
    ) {
        Objects.requireNonNull(language, "language must not be null!");

        MDC.put("project", project);
        MDC.put("language", language);

        try {
            UploadAssetsResponse result = new UploadAssetsResponse();

            String originalFileName = multipartFile.getOriginalFilename();

//            String contentType = multipartFile.getContentType();

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
                    Asset asset = readAsset(language, originalFileName, multipartFile.getSize(), is);

                    result.registerSuccess(asset);
                } catch (IOException | IllegalArgumentException ex) {
                    if (logger.isErrorEnabled())
                        logger.error("Unable to read asset {}!", originalFileName, ex);
                    result.registerFail(originalFileName, ex);
                }
            }

            return result;
        } finally {
            MDC.remove("language");
            MDC.remove("project");
        }
    }

    @RequestMapping("/uploadUrl")
    public UploadAssetsResponse uploadUrl(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(RequestParameters.ASSET_FILENAME) String filename,
            @RequestParam(RequestParameters.ASSET_SOURCE_URL) String sourceUrl
    ) {
        Objects.requireNonNull(language, "language must not be null!");

        MDC.put("project", project);
        MDC.put("language", language);

        if (logger.isDebugEnabled())
            logger.debug("Uploading asset from URL '{}' for filename '{}' and language '{}'", sourceUrl, filename, language);

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
                if (logger.isDebugEnabled()) logger.debug("Source image loaded from URL to temp file ‘{}‘", tmp.getAbsolutePath());
                try (InputStream is = new BufferedInputStream(new FileInputStream(tmp))) {
                    Asset asset = readAsset(language, filename, tmp.length(), is, new FixAssetOrientationOption());

                    result.registerSuccess(asset);
                } catch (IOException | IllegalArgumentException ex) {
                    if (logger.isErrorEnabled())
                        logger.error("Unable to read asset {}!", filename, ex);
                    result.registerFail(filename, ex);
                }
            }

            return result;
        } finally {
            MDC.remove("language");
            MDC.remove("project");
        }
    }

    @RequestMapping("/uploadBulk")
    public UploadAssetsResponse uploadBulk(
            @PathVariable String project,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(RequestParameters.ASSET_SOURCE_URL) String sourceUrl
    ) {
        Objects.requireNonNull(language, "language must not be null!");

        MDC.put("project", project);
        MDC.put("language", language);

        try {
            UploadAssetsResponse result = new UploadAssetsResponse();

            URL url;
            try {
                url = new URL(sourceUrl);
            } catch (MalformedURLException ex) {
                if (logger.isErrorEnabled()) logger.error("Invalid bulk source url '{}'!", sourceUrl, ex);
                result.registerGeneralFailure(ex);
                return result;
            }

            try (ZipInputStream zis = new ZipInputStream(url.openStream())) {
                ZipEntry zipEntry = zis.getNextEntry();
                while (zipEntry != null) {
                    String filename = zipEntry.getName();

                    ByteArrayOutputStream baos = null;
                    ByteArrayInputStream bais = null;
                    try {
                        baos = new ByteArrayOutputStream();
                        IOUtils.copyLarge(zis, baos);
                        bais = new ByteArrayInputStream(baos.toByteArray());
                        Asset asset = readAsset(language, filename, zipEntry.getSize(), bais, new FixAssetOrientationOption());
                        bais.close();
                        baos.close();

                        result.registerSuccess(asset);
                    } catch (IllegalArgumentException ex) {
                        if (logger.isErrorEnabled())
                            logger.error("Unable to read bulk asset {}!", filename, ex);
                        result.registerFail(filename, ex);
                    } finally {
                        IOUtils.closeQuietly(baos);
                        IOUtils.closeQuietly(bais);
                    }

                    zipEntry = zis.getNextEntry();
                }

            } catch (IOException ex) {
                if (logger.isWarnEnabled()) logger.warn("Error processing zip archive", ex);
                result.registerGeneralFailure(ex);
            }

            return result;
        } finally {
            MDC.remove("language");
            MDC.remove("project");
        }
    }

    @RequestMapping(value = "/thumbnail/{dimension}", method = RequestMethod.GET)
    public void getThumbnail(
            @PathVariable String project,
            @PathVariable(RequestParameters.ASSET_DIMENSION) String dimensionName,
            @RequestParam(RequestParameters.LANGUAGE) String language,
            @RequestParam(RequestParameters.ASSET_ID) String assetId,
            @RequestHeader(value = RequestResponseConstants.IF_NONE_MATCH_REQUEST_HEADER, required = false) String etag,
            HttpServletResponse response
    ) throws IOException {

        MDC.put("project", project);
        MDC.put("language", language);

        Dimension desiredDimension = dimensions.get(dimensionName);
        if (desiredDimension == null) {
            throw new IllegalArgumentException("No dimension found for name '" + dimensionName + "'! Available names are: " + dimensions.keySet());
        }

        Asset asset = assetRepository.retrieveAsset(language, assetId);
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
        } finally {
            MDC.remove("language");
            MDC.remove("project");
        }
    }

    private Asset readAsset(
            String language,
            String originalFileName,
            long fileSize,
            InputStream inputStream,
            AssetCreatingOption... options
    ) {
        String fileName = Assets.resolveSanitizedBasename(originalFileName);
        String mimeType = MimeTypes.resolveMimeType(originalFileName);
        if (logger.isDebugEnabled())
            logger.debug("Reading asset from file '{}' (mimeType: {})", originalFileName, mimeType);

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
            byte[] bytes;
            if (options == null) {
                Asset asset = createAsset(language, originalFileName, mimeType, inputStream);
                assetRepository.updateAsset(language, asset);
                return asset;
            }

            bytes = org.apache.commons.io.IOUtils.toByteArray(inputStream);
            InputStream assetInputStream = new ByteArrayInputStream(bytes);
            Asset originalAsset = createAsset(language, originalFileName, mimeType, assetInputStream);

            BufferedImage destinationImage = null;
            for (AssetCreatingOption option : options) {
                if (option instanceof FixAssetOrientationOption) {
                    if (logger.isDebugEnabled()) logger.debug("Fixing asset orientation");
                    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                    ImageUtils.ImageInformation imageInfo = ImageUtils.readImageInformation(bais);
                    if (imageInfo.orientation == 1) {
                        if (logger.isDebugEnabled())
                            logger.debug("Skipping rotating asset, because it's orientation is '1'");
                        continue;
                    }

                    AffineTransform transform = ImageUtils.getExifTransformation(imageInfo);

                    bais = new ByteArrayInputStream(bytes);
                    BufferedImage image = ImageIO.read(bais);
                    BufferedImage fixedImage = ImageUtils.transformImage(image, transform);

                    destinationImage = ImageUtils.copyImage(fixedImage);
                    continue;
                }

                if (option instanceof ResizeAssetOption) {
                    int maxDimension = ((ResizeAssetOption) option).getMaxDimension();

                    if (!ImageAsset.class.isAssignableFrom(originalAsset.getClass())) {
                        if (logger.isDebugEnabled())
                            logger.debug("Skipping resizing asset, because it is not an ImageAsset and don't have any dimensions");
                        continue;
                    }

                    ImageAsset imageAsset = (ImageAsset) originalAsset;
                    Dimension size = imageAsset.getSize();
                    if (size.getHeight() <= maxDimension && size.getWidth() <= maxDimension) {
                        if (logger.isDebugEnabled())
                            logger.debug("Skipping resizing asset, because both it's dimensions are less than maxDimension {}", maxDimension);
                        continue;
                    }

                    if (logger.isDebugEnabled())
                        logger.debug("Resizing asset to fit to maxDimension {}", maxDimension);
                    // TODO: implement me
                }
            }

            if (destinationImage == null) {
                if (logger.isDebugEnabled()) logger.debug("Original asset is unchanged");
                assetRepository.updateAsset(language, originalAsset);
                return originalAsset;
            }

            if (logger.isDebugEnabled()) logger.debug("Modified image: {}", destinationImage);

            if (logger.isDebugEnabled())
                logger.debug("Store both original & modified assets, linked together through the attribute '{}'",
                        Asset.BASE_ASSET_REFERENCE_ATTRIBUTE_NAME);

            ByteArrayInputStream bais;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                String format = MimeTypes.resolveFileExtension(mimeType);
                ImageIO.write(destinationImage, format, baos);
                baos.flush();
                byte[] modifiedAssetBytes = baos.toByteArray();
                if (logger.isDebugEnabled()) logger.debug("Modified asset size: {} bytes", modifiedAssetBytes.length);
                bais = new ByteArrayInputStream(modifiedAssetBytes);
            }
            Asset modifiedAsset = createAsset(language, originalFileName, mimeType, bais);

            modifiedAsset.setAttribute(Asset.BASE_ASSET_REFERENCE_ATTRIBUTE_NAME, originalAsset.getId());

            assetRepository.updateAsset(language, originalAsset);
            assetRepository.updateAsset(language, modifiedAsset);
            return modifiedAsset;
        } catch (IOException | MetadataException | ImageProcessingException ex) {
            throw new IllegalArgumentException("Unable to create asset!", ex);
        }
    }

    private Asset createAsset(String language, String fileName, String mimeType, final InputStream inputStream) throws IOException {
        ImageAsset asset = assetRepository.searchOrCreateAsset(ImageAsset.class, language, inputStream);

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
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error reading asset data!", ex);
        }

        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException("Unsupported mimeType " + mimeType + " detected");
        }

        asset.setMimeType(mimeType);

        if (logger.isDebugEnabled()) logger.debug("New asset: {}", asset);

        return asset;
    }

    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream("/tmp/zip/files.zip"))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String filename = zipEntry.getName();
                builder.append("\tfilename: ").append(filename);
                String sanitizedName = Assets.resolveSanitizedBasename(filename);
                builder.append(", sanitizedName: ").append(sanitizedName);

                String mimeType = MimeTypes.resolveMimeType(filename);
                Objects.requireNonNull(mimeType, "mimeType must not be null!");
                String fileExtension = MimeTypes.resolveFileExtension(mimeType);
                Objects.requireNonNull(fileExtension, "fileExtension must not be null!");

                File file = new File("/tmp/zip/" + sanitizedName + "." + fileExtension);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    IOUtils.copyLarge(zis, fos);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                builder.append(".\n");

                zipEntry = zis.getNextEntry();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println(builder.toString());
    }
}

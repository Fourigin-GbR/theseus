package com.fourigin.argo.controller.assets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.assets.models.Asset;
import com.fourigin.argo.assets.models.Assets;
import com.fourigin.argo.assets.models.ImageAsset;
import com.fourigin.argo.assets.repository.AssetRepository;
import com.fourigin.argo.controller.compile.RequestParameters;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Controller
@RequestMapping("/assets")
public class AssetsController {

    private final Logger logger = LoggerFactory.getLogger(AssetsController.class);

    private AssetRepository assetRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Set<String> ALLOWED_MIME_TYPES;

    static {
        ALLOWED_MIME_TYPES = new HashSet<>();
        ALLOWED_MIME_TYPES.add("image/gif");
        ALLOWED_MIME_TYPES.add("image/jpeg");
        ALLOWED_MIME_TYPES.add("image/png");
    }

    public AssetsController(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @RequestMapping("/")
    @ResponseBody
    public Asset resolveAsset(
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam("id") String assetId
    ) {
        Asset asset = assetRepository.retrieveAsset(base, assetId);

        if (logger.isDebugEnabled()) logger.debug("Returning asset {} for id {}", asset, assetId);

        return asset;
    }

    @RequestMapping(name = "/upload", method = RequestMethod.POST)
    public void upload(
        @RequestParam(RequestParameters.BASE) String base,
        @RequestParam("file") MultipartFile multipartFile,
        HttpServletResponse response
    ) {
        Objects.requireNonNull(base, "base must not be null!");

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

        if(tmp != null) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(tmp))) {
                Asset asset = readAsset(base, originalFileName, multipartFile.getSize(), contentType, is);

                result.registerSuccess(asset);
            } catch (IOException | IllegalArgumentException ex) {
                if (logger.isErrorEnabled())
                    logger.error("Unable to read asset {}!", originalFileName, ex);
                result.registerFail(originalFileName, ex);
            }
        }

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            objectMapper.writeValue(outputStream, result);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) logger.error("Error uploading resource file!", ex);
        }
    }

    private Asset readAsset(String base, String originalFileName, long fileSize, String contentType, InputStream inputStream) {
        String fileName = Assets.resolveSanitizedBasename(originalFileName);
        String mimeType = Assets.resolveMimeType(originalFileName);
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
            return createAsset(base, originalFileName, mimeType, inputStream);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to create asset!", ex);
        }
    }

    private Asset createAsset(String base, String fileName, String mimeType, final InputStream inputStream) throws IOException {

        ImageAsset asset = assetRepository.searchOrCreateAsset(ImageAsset.class, base, inputStream);

        String assetId = asset.getId();
        if (logger.isDebugEnabled()) logger.debug("Asset-ID: {}", assetId);

//        String username = userAbstraction.resolveUsername();
//
//        Set<String> blobResourceIds = resourceRepository.findResourceIdsByBlobId(ImageResource.class, blobId);
//        if (blobResourceIds != null && !blobResourceIds.isEmpty())
//        {
//            String id = blobResourceIds.iterator().next();
//            if (logger.isDebugEnabled()) logger.debug("Taking the first existing resource with id {} for blobId {}.", id, blobId);
//            Asset asset = resourceRepository.retrieveResource(ImageResource.class, id);
//
//            resource.setAttributes(Assets.changedBy(username, timestamp, resource.getAttributes()));
//
//            // add additional locales if necessary, i.e. if not already global
//            Set<String> previousLocaleCodes = resource.getLocaleCodes();
//            if (previousLocaleCodes != null && !previousLocaleCodes.isEmpty())
//            {
//                if (localeCodes != null)
//                {
//                    if (logger.isDebugEnabled()) logger.debug("The previous locales: '{}' and the locales are not null: '{}'", previousLocaleCodes, localeCodes);
//                    previousLocaleCodes.addAll(localeCodes);
//                    resource.setLocaleCodes(previousLocaleCodes);
//                }
//                else
//                {
//                    if (logger.isDebugEnabled()) logger.debug("The previous locales are not null: '{}' but the locales are null. Setting locales to null.", previousLocaleCodes);
//                    resource.setLocaleCodes(null);
//                }
//            }
//            else
//            {
//                if (logger.isDebugEnabled()) logger.debug("The previous locales are null: '{}' but the locales are not null", localeCodes);
//                resource.setLocaleCodes(localeCodes);
//            }
//
//            if (logger.isDebugEnabled()) logger.debug("Updated resource: {}", resource);
//            return resource;
//        }

        // filename
        asset.setName(Assets.resolveSanitizedBasename(fileName));

        // mime type
        if (logger.isDebugEnabled()) logger.debug("File name: '{}'; MIME type: '{}'", fileName, mimeType);

        try (InputStream data = assetRepository.retrieveAssetData(base, assetId)) {
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
                    String mimeByReader = Assets.resolveMimeType("foo." + formatName);
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

//        asset.setAttributes(Assets.createdBy(username, timestamp, asset.getAttributes()));

//        // set locales
//        resource.setLocaleCodes(localeCodes);
//        if (logger.isDebugEnabled())
//            logger.debug("Setting localeCodes '{}' for the new created resource '{}'", resource.getLocaleCodes(), resource.getId());

        if (logger.isDebugEnabled()) logger.debug("New asset: {}", asset);

        return asset;
    }
}

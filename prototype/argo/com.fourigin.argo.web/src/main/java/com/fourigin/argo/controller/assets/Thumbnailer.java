package com.fourigin.argo.controller.assets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Thumbnailer {
    private final Logger logger = LoggerFactory.getLogger(Thumbnailer.class);

    public BufferedImage readImage(InputStream input) throws IOException {
		try(InputStream is = input)
		{
			return ImageIO.read(is);
		}
    }

    // TODO: implement stuff below...
    public BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight) {
        int type;
        boolean isTranslucent;
        if (img.getTransparency() == Transparency.OPAQUE) {
            type = BufferedImage.TYPE_INT_RGB;
            isTranslucent = false;
        } else {
            type = BufferedImage.TYPE_INT_ARGB;
            isTranslucent = true;
        }
        BufferedImage ret = img;
        BufferedImage scratchImage = null;
        Graphics2D g2 = null;
        int w;
        int h;
        int prevW = ret.getWidth();
        int prevH = ret.getHeight();

        boolean progressiveBilinear;
        Object hint;
        if (prevW > targetWidth || prevH > targetHeight) {
            // activate progressiveBilinear if we are downscaling.
            progressiveBilinear = true;
            hint = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        } else {
            progressiveBilinear = false;
            hint = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        }
        if (progressiveBilinear) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (progressiveBilinear && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (progressiveBilinear && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            if (scratchImage == null || isTranslucent) {
                // Use a single scratch buffer for all iterations
                // and then copy to the final, correctly-sized image
                // before returning
                scratchImage = new BufferedImage(w, h, type);
                g2 = scratchImage.createGraphics();
            }
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);
            prevW = w;
            prevH = h;

            ret = scratchImage;
        }
        while (w != targetWidth || h != targetHeight);

        if (g2 != null) {
            g2.dispose();
        }

        // If we used a scratch buffer that is larger than our target size,
        // create an image of the right size and copy the results into it
        if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight()) {
            scratchImage = new BufferedImage(targetWidth, targetHeight, type);
            g2 = scratchImage.createGraphics();
            g2.drawImage(ret, 0, 0, null);
            g2.dispose();
            ret.flush();
            ret = scratchImage;
        }

        return ret;
    }

    public void createThumbnail(BufferedImage original, OutputStream thumbnailOutput, Dimension desiredSize)
            throws IOException {
        createThumbnail(original, thumbnailOutput, desiredSize, null);
    }

    public void createThumbnail(BufferedImage original, OutputStream thumbnailOutput, Dimension desiredSize, Color bgColor)
            throws IOException {
        if (original == null) {
            String msg = "Couldn't load original image. Probably unsupported or wrong format?";
            IOException ex = new IOException(msg);
            if (logger.isWarnEnabled()) logger.warn(msg, ex);
            throw ex;
        }
        Dimension originalSize = new Dimension(original.getWidth(), original.getHeight());
        if (logger.isInfoEnabled()) logger.info("Size of original image: " + originalSize);
        double widthFactor = desiredSize.getWidth() / originalSize.getWidth();
        if (logger.isDebugEnabled()) logger.debug("widthFactor=" + widthFactor);
        double heightFactor = desiredSize.getHeight() / originalSize.getHeight();
        if (logger.isDebugEnabled()) logger.debug("heightFactor=" + heightFactor);
        double realFactor = widthFactor;
        if (heightFactor < realFactor) {
            realFactor = heightFactor;
        }
        if (logger.isDebugEnabled()) logger.debug("realFactor=" + realFactor);
        Dimension newSize = new Dimension((int) (originalSize.getWidth() * realFactor), (int) (originalSize.getHeight() * realFactor));
        if (logger.isDebugEnabled()) logger.debug("newSize=" + newSize);

        BufferedImage scaledImage = getScaledInstance(original, newSize.width, newSize.height);

        int x = (int) ((desiredSize.getWidth() - newSize.getWidth()) / 2);
        int y = (int) ((desiredSize.getHeight() - newSize.getHeight()) / 2);
        Point offset = new Point(x, y);
        if (logger.isDebugEnabled()) logger.debug("offset=" + offset);
        int resultImageType = BufferedImage.TYPE_INT_ARGB;
        if (bgColor != null && bgColor.getTransparency() == Transparency.OPAQUE) {
            resultImageType = BufferedImage.TYPE_INT_RGB;
        }
        BufferedImage resultImage = new BufferedImage((int) desiredSize.getWidth(), (int) desiredSize.getHeight(), resultImageType);
        Graphics2D g = resultImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        if (bgColor != null) {
            g.setColor(bgColor);
            g.fillRect(0, 0, desiredSize.width, desiredSize.height);
        }
        g.drawImage(scaledImage, x, y, null);//, (int) newSize.getWidth(), (int) newSize.getHeight(), null);
        g.dispose();
        scaledImage.flush();

        final String format = "png";
        try(OutputStream outputStream = thumbnailOutput) {
            boolean writerFound = ImageIO.write(resultImage, format, outputStream);

            resultImage.flush();
            if (!writerFound) {
                String msg = "Couldn't write scaled image! No writer found for format '" + format + "'!";
                IOException ex = new IOException(msg);
                if (logger.isWarnEnabled()) logger.warn(msg, ex);
                throw ex;
            }
        }
    }
}

package com.fourigin.utilities.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * In a server environment, don't forget to run with -Djava.awt.headless=true
 */
public class ImageUtils {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    public static class ImageInformation {
        public final int orientation;
        public final int width;
        public final int height;

        public ImageInformation(int orientation, int width, int height) {
            this.orientation = orientation;
            this.width = width;
            this.height = height;
        }

        public String toString() {
            return String.format("%dx%d,%d", this.width, this.height, this.orientation);
        }
    }

    public static ImageInformation readImageInformation(InputStream inputStream)  throws IOException, MetadataException, ImageProcessingException {
        Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
        Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        JpegDirectory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);

        int orientation = 1;
        try {
            orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        } catch (MetadataException me) {
            logger.warn("Could not get orientation");
        }
        int width = jpegDirectory.getImageWidth();
        int height = jpegDirectory.getImageHeight();

        return new ImageInformation(orientation, width, height);
    }

    // Look at http://chunter.tistory.com/143 for information
    public static AffineTransform getExifTransformation(ImageInformation info) {

        AffineTransform t = new AffineTransform();

        switch (info.orientation) {
            case 1:
                break;
            case 2: // Flip X
                t.scale(-1.0, 1.0);
                t.translate(-info.width, 0);
                break;
            case 3: // PI rotation
                t.translate(info.width, info.height);
                t.rotate(Math.PI);
                break;
            case 4: // Flip Y
                t.scale(1.0, -1.0);
                t.translate(0, -info.height);
                break;
            case 5: // - PI/2 and Flip X
                t.rotate(-Math.PI / 2);
                t.scale(-1.0, 1.0);
                break;
            case 6: // -PI/2 and -width
                t.translate(info.height, 0);
                t.rotate(Math.PI / 2);
                break;
            case 7: // PI/2 and Flip
                t.scale(-1.0, 1.0);
                t.translate(-info.height, 0);
                t.translate(0, info.width);
                t.rotate(  3 * Math.PI / 2);
                break;
            case 8: // PI / 2
                t.translate(0, info.width);
                t.rotate(  3 * Math.PI / 2);
                break;
        }

        return t;
    }

    public static BufferedImage transformImage(BufferedImage image, AffineTransform transform) {
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);

        int imageType = image.getType();
        ColorModel colorModel = image.getColorModel();
        BufferedImage destinationImage = op.createCompatibleDestImage(
                image,
                (imageType == BufferedImage.TYPE_BYTE_GRAY) ? colorModel : null
        );

        Graphics2D g = destinationImage.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());
        destinationImage = op.filter(image, destinationImage);
        return destinationImage;
    }

    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public static BufferedImage fixImageOrientation(InputStream inputStream) throws ImageProcessingException, MetadataException, IOException {
        ImageInformation imageInfo = readImageInformation(inputStream);
        AffineTransform transform = getExifTransformation(imageInfo);
        BufferedImage image = ImageIO.read(inputStream);
        BufferedImage fixedImage = transformImage(image, transform);

        return copyImage(fixedImage);
    }

    public static void main(String[] args) throws ImageProcessingException, MetadataException, IOException {
        File imageFile = new File("/tmp/foto.jpg");
        File targetFile = new File("/tmp/foto_fixed.jpg");

//        ImageMetadata imagingMetadata = Imaging.getMetadata(imageFile);


//        ImageReader reader = ImageIO.getImageReadersByMIMEType("image/JPEG").next();
//        reader.setInput(ImageIO.createImageInputStream(imageFile));

//        IIOMetadata iioMetadata = reader.getImageMetadata(0);
//        Node root = iioMetadata.getAsTree(iioMetadata.getNativeMetadataFormatName());
//        BufferedImage image = reader.read(0);

        ImageInformation imageInfo = readImageInformation(new FileInputStream(imageFile));
        AffineTransform transform = getExifTransformation(imageInfo);
        BufferedImage image = ImageIO.read(imageFile);
        BufferedImage fixedImage = transformImage(image, transform);

        BufferedImage destinationImage = copyImage(fixedImage);

        ImageIO.write(destinationImage, "jpeg", targetFile);

//        FileOutputStream os = new FileOutputStream(targetFile);
//        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
//
//        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
//        ImageWriter writer = iter.next();
//        writer.setOutput(ios);
//
////You may want also to alter jpeg quality
//        ImageWriteParam iwParam = writer.getDefaultWriteParam();
//        iwParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//        iwParam.setCompressionQuality(.95f);
//
////Note: we're using metadata we've already saved.
//        writer.write(null, new IIOImage(destinationImage, null, metadata), iwParam);
//        writer.dispose();
//
//        ImageIO.write(destinationImage, "jpg", ios);
    }
}

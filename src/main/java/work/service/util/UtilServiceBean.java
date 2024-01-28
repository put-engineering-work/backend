package work.service.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Service
public class UtilServiceBean implements UtilService {

    public byte[] compressImage(MultipartFile file, float compressionQuality) {
        try {
            String fileExtension = getFileExtension(file);
            InputStream input = file.getInputStream();
            BufferedImage image = ImageIO.read(input);

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(fileExtension);
            if (!writers.hasNext())
                throw new IllegalStateException("No writers found for the format: " + fileExtension);

            ImageWriter writer = writers.next();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(compressionQuality); // 0.0 - 1.0, 1.0 for highest quality
            }
            writer.write(null, new IIOImage(image, null, null), param);

            ios.close();
            outputStream.close();
            input.close();
            writer.dispose();
            return outputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public byte[] decompressImage(byte[] compressedImageData) {
        ByteArrayInputStream inputStream = null;
        byte[] imageBytes = null;
        try {
            inputStream = new ByteArrayInputStream(compressedImageData);
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);

            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
            if (!imageReaders.hasNext()) {
                throw new IOException("No image reader found for given format.");
            }

            ImageReader reader = imageReaders.next();
            String formatName = reader.getFormatName();

            BufferedImage image = ImageIO.read(imageInputStream);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, formatName, outputStream);

            imageBytes = outputStream.toByteArray();

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
        return imageBytes;
    }

    private String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.lastIndexOf(".") != -1) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            throw new IllegalArgumentException("Invalid file name or extension");
        }
    }

}

package work.service.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Service
public class UtilServiceBean implements UtilService{
        public byte[] compressImage(MultipartFile file, float compressionQuality) throws IOException {
            InputStream input = file.getInputStream();
            BufferedImage image = ImageIO.read(input);


            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (!writers.hasNext()) throw new IllegalStateException("No writers found");

            ImageWriter writer = writers.next();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
            writer.setOutput(ios);

            // Настройка параметров сжатия
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(compressionQuality); // 0.0 - 1.0, 1.0 - максимальное качество

            // Сжатие и запись изображения
            writer.write(null, new IIOImage(image, null, null), param);

            // Закрытие потоков
            ios.close();
            outputStream.close();
            input.close();
            writer.dispose();

            return outputStream.toByteArray();
        }

}

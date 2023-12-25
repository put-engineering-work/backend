package work.service.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UtilService {
    byte[] compressImage(MultipartFile file, float compressionQuality) throws IOException;
}

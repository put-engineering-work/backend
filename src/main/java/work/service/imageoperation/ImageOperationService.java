package work.service.imageoperation;

import org.springframework.web.multipart.MultipartFile;

public interface ImageOperationService {
    byte[] compressImage(MultipartFile file, float compressionQuality);
}

package work.service.imageoperation;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageOperationService {
    public byte[] compressImage(MultipartFile file, float compressionQuality);
}

package net.likelion.bebc25.itda.s3;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

@Component
public class ImageValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png"
    );

    public void validate(MultipartFile file) {

        // 파일 존재 여부
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 파일 크기
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("이미지는 최대 5MB까지 업로드할 수 있습니다.");
        }

        // Content-Type
        String contentType = file.getContentType();

        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다.");
        }

        // 실제 이미지인지 확인
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            if (image == null) {
                throw new IllegalArgumentException("유효한 이미지 파일이 아닙니다.");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("이미지 파일을 확인할 수 없습니다.", e);
        }
    }
}
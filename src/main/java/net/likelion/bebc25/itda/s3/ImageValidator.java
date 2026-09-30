package net.likelion.bebc25.itda.s3;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

@Component
public class ImageValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
        try {
            validate(file.getBytes(), file.getContentType());
        } catch (IOException e) {
            throw new IllegalArgumentException("이미지 파일을 확인할 수 없습니다.", e);
        }
    }

    public void validate(byte[] bytes, String contentType) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
        if (bytes.length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("이미지는 최대 5MB까지 업로드할 수 있습니다.");
        }

        String normalized = normalizeContentType(contentType);
        if (!ALLOWED_CONTENT_TYPES.contains(normalized)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다.");
        }

        // ImageIO는 기본 JDK에서 webp를 못 읽으므로 매직바이트만 확인
        if ("image/webp".equals(normalized)) {
            if (!isWebp(bytes)) {
                throw new IllegalArgumentException("유효한 이미지 파일이 아닙니다.");
            }
            return;
        }

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) {
                throw new IllegalArgumentException("유효한 이미지 파일이 아닙니다.");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("이미지 파일을 확인할 수 없습니다.", e);
        }
    }

    private static String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "";
        }
        int semicolon = contentType.indexOf(';');
        String base = semicolon >= 0 ? contentType.substring(0, semicolon) : contentType;
        return base.trim().toLowerCase();
    }

    /** RIFF....WEBP */
    private static boolean isWebp(byte[] bytes) {
        if (bytes.length < 12) {
            return false;
        }
        return bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P';
    }
}

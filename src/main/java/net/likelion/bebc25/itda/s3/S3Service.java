package net.likelion.bebc25.itda.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final ImageValidator imageValidator;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file, String directory) {
        imageValidator.validate(file);

        String contentType = normalizeContentType(file.getContentType());
        String extension = getExtension(contentType);
        String key = directory + "/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );
            return key;
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드에 실패했습니다.", e);
        }
    }

    /** 바이트 배열을 검증 후 S3에 업로드하고 key를 반환한다. */
    public String upload(byte[] bytes, String contentType, String directory) {
        String normalized = normalizeContentType(contentType);
        imageValidator.validate(bytes, normalized);

        String extension = getExtension(normalized);
        String key = directory + "/" + UUID.randomUUID() + extension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(normalized)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(bytes));
        return key;
    }

    /**
     * 외부 이미지 URL을 내려받아 profile 디렉터리에 업로드한다.
     * 실패 시 예외를 던져 호출측에서 가입 흐름을 유지할지 결정한다.
     */
    public String uploadFromUrl(String imageUrl, String directory) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "image/*,*/*")
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalArgumentException("이미지 URL 응답이 올바르지 않습니다. status=" + response.statusCode());
            }

            String contentType = response.headers()
                    .firstValue("Content-Type")
                    .orElse("");
            // Content-Type이 비어 있으면 URL 확장자로 추정
            if (contentType.isBlank()) {
                contentType = guessContentTypeFromUrl(imageUrl);
            }

            return upload(response.body(), contentType, directory);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("외부 이미지 다운로드에 실패했습니다.", e);
        }
    }

    public String getPresignedUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(30)) // 30분 유효
                        .getObjectRequest(getObjectRequest)
                        .build();

        return s3Presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }

    public void delete(String key) {
        if (key == null || key.isBlank()) {
            return;
        }

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    private String getExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException(
                    "지원하지 않는 이미지 형식입니다."
            );
        };
    }

    private static String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "";
        }
        int semicolon = contentType.indexOf(';');
        String base = semicolon >= 0 ? contentType.substring(0, semicolon) : contentType;
        return base.trim().toLowerCase();
    }

    private static String guessContentTypeFromUrl(String imageUrl) {
        String path = imageUrl;
        int query = path.indexOf('?');
        if (query >= 0) {
            path = path.substring(0, query);
        }
        String lower = path.toLowerCase();
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "image/jpeg";
    }
}

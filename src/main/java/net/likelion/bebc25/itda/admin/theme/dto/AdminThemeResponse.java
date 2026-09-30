package net.likelion.bebc25.itda.admin.theme.dto;

import java.time.LocalDateTime;

public record AdminThemeResponse(
        Long id,
        String themeName,
        String description,
        Integer price,
        String thumbnailUrl,
        String themeCode,
        String status,
        Boolean isDefault,
        LocalDateTime updatedAt,
        LocalDateTime createdAt,
        String cssText
) {
    /** DB의 S3 key를 프리사인 URL로 바꿔 응답한다 */
    public static AdminThemeResponse from(AdminThemeResponse theme, String thumbnailUrl) {
        return new AdminThemeResponse(
                theme.id(),
                theme.themeName(),
                theme.description(),
                theme.price(),
                thumbnailUrl,
                theme.themeCode(),
                theme.status(),
                theme.isDefault(),
                theme.updatedAt(),
                theme.createdAt(),
                theme.cssText()
        );
    }
}

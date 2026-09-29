package net.likelion.bebc25.itda.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ThemeStylesResponse(
        @Schema(description = "테마 ID", example = "3")
        Long themeId,

        @Schema(description = "프론트 data-theme 키", example = "ocean")
        String themeCode,

        @Schema(description = "인가된 테마 CSS 규칙")
        String cssText
) {
    /** Mapper 조회용 내부 row → API 응답 */
    public static ThemeStylesResponse of(ThemeStyleRow row) {
        return new ThemeStylesResponse(row.id(), row.themeCode(), row.cssText());
    }

    /** MyBatis 조회 전용 (API로 직접 노출하지 않음) */
    public record ThemeStyleRow(
            Long id,
            String themeCode,
            String cssText,
            Boolean isDefault
    ) {}
}
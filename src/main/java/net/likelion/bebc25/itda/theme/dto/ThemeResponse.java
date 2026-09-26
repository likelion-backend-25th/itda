package net.likelion.bebc25.itda.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ThemeResponse(
        @Schema(description = "테마 고유 식별자(PK)", example = "1")
        Long id,

        @Schema(description = "테마 이름", example = "기본 화이트")
        String themeName,

        @Schema(description = "가격", example = "0")
        int price,

        @Schema(description = "테마 미리보기 이미지 url", example = "theme/1/thumbnail.webp")
        String thumbnailUrl,

        @Schema(description = "테마 코드", example = "basic-01")
        String themeCode,

        @Schema(description = "보유 중인지", example = "true / false")
        Boolean isOwned,
        @Schema(description = "적용 중인지", example = "true / false")
        Boolean isApplied

) { }
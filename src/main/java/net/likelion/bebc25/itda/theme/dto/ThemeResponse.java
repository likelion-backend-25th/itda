package net.likelion.bebc25.itda.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ThemeResponse(
        @Schema(description = "테마 고유 식별자(PK)", example = "1")
        Long id,

        @Schema(description = "테마 이름", example = "기본 화이트")
        String themeName,

        @Schema(description = "테마 설명", example = "ITDA 기본 화이트 테마")
        String description,

        @Schema(description = "가격", example = "0")
        int price,

        @Schema(description = "테마 미리보기 이미지 url", example = "theme/1/thumbnail.webp")
        String thumnailUrl,

        @Schema(description = "테마 코드", example = "basic-01")
        String themeCode,

        @Schema(description = "테마 노출/판매 상태", example = "ON_SALE/HIDDEN, default:HIDDEN")
        String status,

        @Schema(description = "기본 테마인 경우 true", example = "true / false")
        Boolean is_default,

        @Schema(description = "테마 수정 일시", example = "2026-08-07T10:30:00")
        LocalDateTime updatedAt,

        @Schema(description = "테마 등록 일시", example = "2026-08-07T10:00:00")
        LocalDateTime createdAt
) { }

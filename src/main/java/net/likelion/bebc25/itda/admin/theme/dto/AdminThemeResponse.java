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
        LocalDateTime createdAt
) {}
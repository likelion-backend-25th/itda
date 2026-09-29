package net.likelion.bebc25.itda.admin.theme.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminThemeRequest(

        @NotBlank(message = "테마 이름은 필수입니다.")
        @Size(max = 100, message = "테마 이름은 100자 이하여야 합니다.")
        String themeName,

        @Size(max = 255, message = "테마 설명은 255자 이하여야 합니다.")
        String description,

        @NotNull(message = "테마 가격은 필수입니다.")
        @Min(value = 0, message = "테마 가격은 0원 이상이어야 합니다.")
        Integer price,

        @Size(max = 255, message = "썸네일 URL은 255자 이하여야 합니다.")
        String thumbnailUrl,

        @NotBlank(message = "테마 코드는 필수입니다.")
        @Size(max = 255, message = "테마 코드는 255자 이하여야 합니다.")
        String themeCode,

        String cssText
) {}
package net.likelion.bebc25.itda.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

public record PostCreateRequest (

        @NotNull(message = "카테고리 필수입니다.")
        Long categoryId,
        @NotBlank(message = "게시글 내용은 필수입니다.")
        String content,
        String imageUrl,
        @NotNull(message = "게시글 공개 범위를 선택해주세요")
        boolean subscriberOnly
){ }

package net.likelion.bebc25.itda.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

public record PostUpdateRequest (
        @NotNull(message = "카테고리는 필수입니다.")
        Long categoryId,

        @NotNull(message = "게시글 내용은 필수입니다.")
        String content,

        String imageUrl,

        @NotNull(message = "게시글 공개 범위를 선택해주세요")
        Boolean subscriberOnly

){}


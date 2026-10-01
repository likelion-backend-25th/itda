package net.likelion.bebc25.itda.reply.dto;

import jakarta.validation.constraints.NotBlank;

public record ReplyCreateRequest (
        @NotBlank(message = "댓글 내용은 필수입니다.")
        String content
){ }

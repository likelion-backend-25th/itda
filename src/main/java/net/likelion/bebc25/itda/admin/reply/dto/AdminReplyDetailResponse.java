package net.likelion.bebc25.itda.admin.reply.dto;

import java.time.LocalDateTime;

public record AdminReplyDetailResponse(
        Long id,
        Long postId,
        String nickname,
        String email,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

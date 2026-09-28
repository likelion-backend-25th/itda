package net.likelion.bebc25.itda.admin.reply.dto;

import java.time.LocalDateTime;

public record AdminReplyResponse(
        Long id,
        String nickname,
        String email,
        String content,
        LocalDateTime createdAt
) {}
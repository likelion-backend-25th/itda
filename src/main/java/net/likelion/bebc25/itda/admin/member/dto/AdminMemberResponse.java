package net.likelion.bebc25.itda.admin.member.dto;

import java.time.LocalDateTime;

public record AdminMemberResponse(
        Long id,
        String email,
        String nickname,
        String role,
        String status,
        String profileImage,
        Long themeId,
        String authmethod,
        LocalDateTime createdAt
) {}
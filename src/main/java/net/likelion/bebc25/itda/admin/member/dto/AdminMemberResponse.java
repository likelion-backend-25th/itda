package net.likelion.bebc25.itda.admin.member.dto;

import java.time.LocalDateTime;

public record AdminMemberResponse(
        Long id,
        String profileImage,
        String nickname,
        String email,
        String authmethod,
        LocalDateTime createdAt
) {}
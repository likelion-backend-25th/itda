package net.likelion.bebc25.itda.member.dto;

public record MemberResponse(
        Long id,
        String nickname,
        String profileImage
) {}
package net.likelion.bebc25.itda.member.dto;

import net.likelion.bebc25.itda.domain.Member;

import java.time.LocalDateTime;
import java.util.List;

// 사용자에게 데이터를 전달 할때 사용하는 dto (민감한 password 는 제외)
public record MemberProfileResponse(
        Long id,
        String email,
        String nickname,
        String profileImage,
        String role,
        String introduction,
        Long themeId,
        LocalDateTime createdAt,
        int followerCount,
        int followingCount,
        int postCount,
        String status,
        List<Long> interestCategoryIds
) {
    public static MemberProfileResponse from(
            Member member,
            String profileImageUrl,
            int followerCount,
            int followingCount,
            int postCount,
            List<Long> interestCategoryIds
    ) {
        return new MemberProfileResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                profileImageUrl,
                member.getRole(),
                member.getIntroduction(),
                member.getThemeId(),
                member.getCreatedAt(),
                followerCount,
                followingCount,
                postCount,
                member.getStatus(),
                interestCategoryIds == null ? List.of() : interestCategoryIds
        );
    }
}

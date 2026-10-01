package net.likelion.bebc25.itda.admin.post.dto;

import java.time.LocalDateTime;

public record AdminPostDetailResponse(
        Long id,
        String nickname,
        String email,
        String categoryName,
        String content,
        String imageUrl,
        Integer replyCount,
        Integer likeCount,
        Integer viewCount,
        Boolean subscriberOnly,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

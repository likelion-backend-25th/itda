package net.likelion.bebc25.itda.post.dto;

import net.likelion.bebc25.itda.post.domain.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        Long memberId,
        String nickname,
        Long categoryId,
        String categoryName,
        String content,
        String imageUrl,
        int likeCount,
        int viewCount,
        boolean subscriberOnly,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PostResponse from (Post post){
        return new PostResponse(
                post.getId(),
                post.getMemberId(),
                post.getNickname(),
                post.getCategoryId(),
                post.getCategoryName(),
                post.getContent(),
                post.getImageUrl(),
                post.getLikeCount(),
                post.getViewCount(),
                post.isSubscriberOnly(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}

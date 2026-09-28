package net.likelion.bebc25.itda.post.dto;

import net.likelion.bebc25.itda.post.domain.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        Long memberId,
        String nickname,
        /** 작성자 프로필 이미지 (프리사인 URL 또는 null) */
        String profileImage,
        Long categoryId,
        String categoryName,
        String content,
        String imageUrl,
        int likeCount,
        int viewCount,
        boolean subscriberOnly,
        boolean liked,
        boolean scrapped,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // 로그인한 사용자가 그 글에 좋아요나 스크랩을 해둔 상태인지 바로 알기 위해서 필요함
    public static PostResponse from(Post post, String profileImageUrl, String imageUrl, boolean liked,  boolean scrapped) {
        return new PostResponse(
                post.getId(),
                post.getMemberId(),
                post.getNickname(),
                profileImageUrl,
                post.getCategoryId(),
                post.getCategoryName(),
                post.getContent(),
                imageUrl,
                post.getLikeCount(),
                post.getViewCount(),
                post.isSubscriberOnly(),
                liked,
                scrapped,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}

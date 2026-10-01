package net.likelion.bebc25.itda.post.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Post {
    private Long id;
    private Long memberId;
    private String nickname;
    /** member.profile_image S3 key (JOIN 조회) */
    private String profileImage;
    private Long categoryId;
    private String categoryName;
    private String content;
    private String imageUrl;
    private int replyCount;
    private int likeCount;
    private int viewCount;
    // 구독자 전용 여부
    private boolean subscriberOnly;
    private boolean liked;
    private boolean scrapped;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

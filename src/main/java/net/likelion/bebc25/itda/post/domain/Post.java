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
    private Long categoryId;
    private String categoryName;
    private String content;
    private String imageUrl;
    private int likeCount;
    private int viewCount;
    // 구독자 전용 여부
    private boolean subscriberOnly;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

package net.likelion.bebc25.itda.reply.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Reply {
    private Long id;
    private Long memberId;
    private String nickname;
    private String profileImage;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

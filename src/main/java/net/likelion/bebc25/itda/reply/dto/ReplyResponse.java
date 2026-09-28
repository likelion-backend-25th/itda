package net.likelion.bebc25.itda.reply.dto;

import net.likelion.bebc25.itda.reply.domain.Reply;

import java.time.LocalDateTime;

public record ReplyResponse (
        Long id,
        Long memberId,
        String nickname,
        String profileImage,
        Long postId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
    public static ReplyResponse from(Reply reply){
        return new ReplyResponse(
                reply.getId(),
                reply.getMemberId(),
                reply.getNickname(),
                reply.getProfileImage(),
                reply.getPostId(),
                reply.getContent(),
                reply.getCreatedAt(),
                reply.getUpdatedAt()
        );
    }
}

package net.likelion.bebc25.itda.reply.service;

import net.likelion.bebc25.itda.reply.dto.ReplyCreateRequest;
import net.likelion.bebc25.itda.reply.dto.ReplyResponse;
import net.likelion.bebc25.itda.reply.dto.ReplyUpdateRequest;

import java.util.List;

public interface ReplyService {
    // 1. 댓글 등록
    ReplyResponse createReply(Long memberId, Long postId, ReplyCreateRequest request);

    // 2. 해당 게시글의 댓글들 조회
    List<ReplyResponse> getRepliesByPostId(Long postId);

    // 3. 댓글 수정
    ReplyResponse updateReply(Long memberId, Long postId, Long replyId, ReplyUpdateRequest request);

    // 4. 댓글 삭제
    void deleteReply(Long memberId, Long postId, Long replyId);
}

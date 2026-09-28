package net.likelion.bebc25.itda.reply.service;

import net.likelion.bebc25.itda.reply.dto.ReplyCreateRequest;
import net.likelion.bebc25.itda.reply.dto.ReplyResponse;

public interface ReplyService {
    ReplyResponse createReply(Long memberId, Long postId, ReplyCreateRequest request);
}

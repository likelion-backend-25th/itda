package net.likelion.bebc25.itda.admin.reply.service;

import net.likelion.bebc25.itda.admin.reply.dto.AdminReplyResponse;

import java.util.List;

public interface AdminReplyService {

    List<AdminReplyResponse> getAllReplies();

    void deleteReply(Long replyId);
}
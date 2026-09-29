package net.likelion.bebc25.itda.admin.reply.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.admin.reply.dto.AdminReplyResponse;
import net.likelion.bebc25.itda.admin.reply.mapper.AdminReplyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReplyServiceImpl implements AdminReplyService {

    private final AdminReplyMapper adminReplyMapper;

    @Override
    public List<AdminReplyResponse> getAllReplies() {
        return adminReplyMapper.findAllReplies();
    }

    @Transactional
    @Override
    public void deleteReply(Long replyId) {
        int deletedCount = adminReplyMapper.deleteReply(replyId);

        if (deletedCount == 0) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }
    }
}
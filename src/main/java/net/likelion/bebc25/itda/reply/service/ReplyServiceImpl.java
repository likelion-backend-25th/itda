package net.likelion.bebc25.itda.reply.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.post.domain.Post;
import net.likelion.bebc25.itda.post.mapper.PostMapper;
import net.likelion.bebc25.itda.reply.domain.Reply;
import net.likelion.bebc25.itda.reply.dto.ReplyCreateRequest;
import net.likelion.bebc25.itda.reply.dto.ReplyResponse;
import net.likelion.bebc25.itda.reply.mapper.ReplyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyServiceImpl implements ReplyService {

    private final ReplyMapper replyMapper;
    private final PostMapper postMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReplyResponse createReply(Long memberId, Long postId, ReplyCreateRequest request) {

        Post post = postMapper.findById(postId);

        // 1. 댓글을 작성할 게시글이 존재하는 지 확인
        if(post == null) {
            throw new NoSuchElementException("존재하지 않는 게시글입니다. id: " +postId);
        }

        // 댓글 객체 생성
        Reply reply = Reply.builder().memberId(memberId).postId(postId).content(request.content()).build();

        // 댓글 저장
        replyMapper.save(reply);

        // 저장된 댓글 다시 조회
        Reply savedReply = replyMapper.findById(reply.getId());

        // 반환
        return ReplyResponse.from(savedReply);



    }
}

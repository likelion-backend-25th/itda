package net.likelion.bebc25.itda.reply.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.domain.Member;
import net.likelion.bebc25.itda.member.mapper.MemberMapper;
import net.likelion.bebc25.itda.post.domain.Post;
import net.likelion.bebc25.itda.post.mapper.PostMapper;
import net.likelion.bebc25.itda.reply.domain.Reply;
import net.likelion.bebc25.itda.reply.dto.ReplyCreateRequest;
import net.likelion.bebc25.itda.reply.dto.ReplyResponse;
import net.likelion.bebc25.itda.reply.dto.ReplyUpdateRequest;
import net.likelion.bebc25.itda.reply.mapper.ReplyMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyServiceImpl implements ReplyService {

    private final ReplyMapper replyMapper;
    private final PostMapper postMapper;
    private final MemberMapper memberMapper;

    /** 활동 정지 회원은 댓글 작성·수정이 불가능하다 */
    private void rejectIfSuspended(Long memberId) {
        Member member = memberMapper.findById(memberId);
        if (member != null && "SUSPENDED".equals(member.getStatus())) {
            throw new IllegalArgumentException("활동 정지된 회원은 댓글을 작성할 수 없습니다.");
        }
    }

    // 1. 댓글 등록
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReplyResponse createReply(Long memberId, Long postId, ReplyCreateRequest request) {
        rejectIfSuspended(memberId);

        Post post = postMapper.findById(postId);

        // 1. 댓글을 작성할 게시글이 존재하는 지 확인
        if(post == null) {
            throw new NoSuchElementException("존재하지 않는 게시글입니다. id: " +postId);
        }

        // 댓글 객체 생성
        Reply reply = Reply.builder().memberId(memberId).postId(postId).content(request.content()).build();

        // 댓글 저장
        replyMapper.save(reply);

        // 댓글 수 증가
        postMapper.increaseReplyCount(postId);

        // 저장된 댓글 다시 조회
        Reply savedReply = replyMapper.findById(reply.getId());

        return ReplyResponse.from(savedReply);

    }

    // 2. 해당 게시글의 댓글 조회
    @Override
    public List<ReplyResponse> getRepliesByPostId(Long postId) {

        Post post = postMapper.findById(postId);

        if(post == null) {
            throw new NoSuchElementException("존재하지 않는 게시글입니다. id: " + postId);
        }
        // 해당 게시글의 댓글 목록 조회
        List<Reply> replies = replyMapper.findByPostId(postId);

        // ReplyResponse로 변환
        List<ReplyResponse> responses = new ArrayList<>();
        for(Reply reply : replies) {
            ReplyResponse response = ReplyResponse.from(reply);

            responses.add(response);
        }
        return responses;
    }

    // 3. 댓글 수정 - 작성자만 수정 가능
    @Override
    @Transactional(rollbackFor = Exception.class)   // 해당 기능이 실패하면 전부 롤백
    @PreAuthorize("@replyServiceImpl.isAuthor(#replyId, authentication.principal.id)")
    public ReplyResponse updateReply(Long memberId, Long postId, Long replyId, ReplyUpdateRequest request){
        rejectIfSuspended(memberId);

        Reply reply = replyMapper.findById(replyId);

        if(reply == null) {
            throw new NoSuchElementException("존재하지 않는 댓글입니다, id: " +replyId);
        }
        // 해당 게시글의 댓글인지 체크 아니면 exception
        if(!reply.getPostId().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다.");
        }

        Reply updateReply = Reply.builder().id(replyId).content(request.content()).build();

        replyMapper.update(updateReply);

        Reply savedReply = replyMapper.findById(replyId);

        return ReplyResponse.from(savedReply);
    }

    // 4. 댓글 삭제 - 관리자와 작성자만 삭제 가능
    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or @replyServiceImpl.isAuthor(#replyId, authentication.principal.id)")
    public void deleteReply(Long memberId, Long postId, Long replyId) {
        Reply reply = replyMapper.findById(replyId);

        if(reply == null) {
            throw new NoSuchElementException("존재하지 않는 댓글입니다. id: " +  replyId);
        }

        if(!reply.getPostId().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다.");
        }

        replyMapper.deleteById(replyId);

        // 댓글 수 감소
        postMapper.decreaseReplyCount(postId);
    }

    // 댓글 작성자 본인 여부를 검증하는 헬퍼 메서드
    public boolean isAuthor(Long replyId, Long memberId) {
        Reply reply = replyMapper.findById(replyId);
        return reply != null && reply.getMemberId().equals(memberId);
    }
}

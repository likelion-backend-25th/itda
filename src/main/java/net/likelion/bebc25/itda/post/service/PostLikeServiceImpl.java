package net.likelion.bebc25.itda.post.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.post.domain.Post;
import net.likelion.bebc25.itda.post.dto.PostLikeResponse;
import net.likelion.bebc25.itda.post.mapper.PostMapper;
import net.likelion.bebc25.itda.post.mapper.PostReactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeServiceImpl implements PostLikeService {

    private final PostMapper postMapper;
    private final PostReactionMapper postReactionMapper;
    private final PostService postService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostLikeResponse toggleLike(Long memberId, Long postId) {

        // 1. 대상 게시글의 존재 여부 확인
        Post post = postMapper.findById(postId);
        // 해당 게시글이 존재하지 않으면
        if(post == null) {
            throw new NoSuchElementException("존재하지 않는 게시글입니다. id: " + postId);
        }

        // 검증 먼저
        postService.validatePostAccess(post, memberId);

        // 내 게시글에는 스크랩 불가능
        // 엑스에서 내 게시글 스크랩 가능
        if(post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인 게시글입니다.");
        }

        // 2. 현재 사용자의 좋아요 등록 여부 확인
        boolean isLiked = postReactionMapper.countLike(memberId,postId) > 0;

        // 3. 이미 등록되어 있을 경우 등록 취소 처리
        if(isLiked) {
            // 좋아요 제거
            postReactionMapper.deleteLike(memberId,postId);
            // 좋아요 수 1 감소
            postMapper.decreaseLikeCount(postId);
            isLiked = false;

        }
        // 4. 좋아요가 없으면 등록
        else{
            postReactionMapper.insertLike(memberId,postId);
            postMapper.increaseLikeCount(postId);
            isLiked = true;

        }
        // 5. 변경된 likeCount를 가져오기 위해 다시 조회
        Post updatedPost = postMapper.findById(postId);

        return new PostLikeResponse(isLiked, updatedPost.getLikeCount());

    }
}

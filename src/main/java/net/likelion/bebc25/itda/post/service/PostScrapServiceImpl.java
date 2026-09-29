package net.likelion.bebc25.itda.post.service;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.itda.post.domain.Post;
import net.likelion.bebc25.itda.post.dto.PostScrapResponse;
import net.likelion.bebc25.itda.post.mapper.PostMapper;
import net.likelion.bebc25.itda.post.mapper.PostReactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostScrapServiceImpl implements PostScrapService {

    private final PostMapper postMapper;
    private final PostReactionMapper postReactionMapper;
    private final PostService postService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostScrapResponse toggleScrap(Long memberId, Long postId) {

        // 게시글 존재 여부 확인
        Post post  =  postMapper.findById(postId);

        if(post == null) {
            throw new NoSuchElementException("존재하지 않는 게시글입니다. id: " + postId);
        }
        // 검증 먼저
        postService.validatePostAccess(post, memberId);

        // 내 게시글에는 좋아요 불가능
        if(post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인 게시글입니다.");
        }

        // 스크랩 여부
        boolean isScrapped = postReactionMapper.countScrap(memberId, postId) > 0;

        // 이미 스크랩 한 경우 취소
        if(isScrapped) {
            postReactionMapper.deleteScrap(memberId, postId);
            isScrapped = false;
        }

        // 스크랩 하지 않은 경우 등록
        else{
            postReactionMapper.insertScrap(memberId, postId);
            isScrapped = true;
        }

        // 변경 후 스크랩 반환
        return new PostScrapResponse(isScrapped);

    }
}
